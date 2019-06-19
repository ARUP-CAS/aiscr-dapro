package cz.ssc.dapro;

import cz.ssc.filter.AccessLevel;
import cz.ssc.filter.Constants;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.http.client.utils.URIBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XsltHandler implements URIResolver {
    private static final Logger LOGGER = Logger.getLogger(XsltHandler.class.getName());

    private static final String TRANSFORM_RESOURCE_DIR = "/xslt/";

    private final HashMap<String, Builder> verb2resource;

    private final FileInput fileInput;

    public XsltHandler() {
        verb2resource = new HashMap<>();
        ListBuilder listBuilder = new ListBuilder();
        verb2resource.put("GetRecord", new RecordBuilder());
        verb2resource.put("Identify", new GreetingBuilder());
        verb2resource.put("ListIdentifiers", listBuilder);
        verb2resource.put("ListMetadataFormats", new MetadataBuilder());
        verb2resource.put("ListRecords", listBuilder);
        verb2resource.put("ListSets", new SetBuilder());

        fileInput = new FileInput();
    }

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        LOGGER.log(Level.FINE, "resolving " + href);

        if (href.startsWith("file:")) {
            return resolveFile(href);
        } else {
            return resolveResource(href);
        }
    }

    private Source resolveFile(String href) throws TransformerException {
        try {
            URIBuilder builder = new URIBuilder(href);
            FilterSpec spec = new FilterSpec(builder);
            builder.clearParameters();
            String path = builder.getPath();
            return fileInput.filter(path, spec);
        } catch (IOException | URISyntaxException ex) {
            LOGGER.log(Level.WARNING, "cannot resolve file URL " + href);
            throw new TransformerException(ex);
        } catch (ProtocolException | SAXException | TransformerConfigurationException ex) {
            LOGGER.log(Level.WARNING, "filtering failed for " + href);
            throw new TransformerException(ex);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "unexpected exception", ex);
            throw new TransformerException(ex);
        }
    }

    private Source resolveResource(String href) throws TransformerException {
        try {
            String resourceName = TRANSFORM_RESOURCE_DIR + href;
            URL xu = getClass().getResource(resourceName);
            return new StreamSource(xu.openStream(), xu.toExternalForm());
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "cannot resolve resource URL " + href);
            throw new TransformerException(ex);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "unexpected exception", ex);
            throw new TransformerException(ex);
        }
    }

    public void process(HttpServletRequest request,
            HttpServletResponse response, Options options)
            throws IOException, TransformerException, MalformedURLException,
                    URISyntaxException, ParserConfigurationException, SAXException, BadAuthException {

        // Getting authorization header
        String auth = request.getHeader("Authorization");

        // Retrive access lvl for user
        String reply = Authenticator.getUserAccess(auth, options.getPassUrl());
        AccessLevel accessLvl = AccessLevel.convert(reply);
        String verb = request.getParameter(Builder.VERB);
        Builder builder = verb2resource.get(verb);

        response.setContentType("text/xml");
        if (builder != null) {
            handle(builder, request, response, options, accessLvl);
        } else {
            handleError(Errors.badVerb, verb, options.getPublicUrl(), request,
                    response);
        }
    }

    void handle(Builder builder, HttpServletRequest request,
            HttpServletResponse response, Options options, AccessLevel lvl)
            throws IOException, TransformerException, MalformedURLException,
                    URISyntaxException, ParserConfigurationException, SAXException {
        try {
            Document stubDoc = builder.makeStubXml(request, options, lvl);
            RequestData requestData = new RequestData(request, options);
            boolean dc = requestData.hasDublinCoreFormat();
            String setClass = requestData.getSetClass();
            String individualClass = requestData.getIndividualClass();

            String recordClass;
            if (individualClass != null) {
                if (dc) {
                    if (!(individualClass.equals(Constants.DOCUMENT) &&
                        (setClass == null || setClass.equals(Constants.DOCUMENT)))) {
                        throw new ProtocolException(Errors.cannotDisseminateFormat, requestData.getPrefix());
                    }
                } else {
                    if ((setClass != null) && !individualClass.equals(setClass)) {
                        throw new ProtocolException(Errors.badArgument, null);
                    }
                }

                recordClass = individualClass;
            } else {
                if (dc) {
                    if ((setClass != null) && !setClass.equals(Constants.DOCUMENT)) {
                        throw new ProtocolException(Errors.cannotDisseminateFormat, requestData.getPrefix());
                    }

                    recordClass = Constants.DOCUMENT;
                } else {
                    recordClass = (setClass != null) ? setClass : requestData.getRecordClass();
                }
            }

            Element params = Builder.getParams(stubDoc);

            if (builder.useSourceUrl()) {
                URL sourceLink = builder.makeSourceUrl(requestData, options,
                        recordClass, requestData.getOffset(), lvl);
                checkLink(sourceLink, requestData.hasResumptionToken());
                Builder.appendLink(params, recordClass, sourceLink.toString());
            } else {
                Builder.appendLink(params, recordClass, "empty.xml");
            }

            if (!dc && (individualClass == null) && (setClass == null)) {
                String nextClass = ResumptionToken.getNextClass(recordClass);
                while (nextClass != null) {
                    URL nextLink = builder.makeSourceUrl(requestData, options,
                            nextClass, 0, lvl);
                    // link not checked - following links may never be opened,
                    // and some testing scenarios actually don't have
                    // the files...
                    Builder.appendLink(params, nextClass, nextLink.toString());
                    nextClass = ResumptionToken.getNextClass(nextClass);
                }
            }

            transform(stubDoc, request, response);
        } catch (ProtocolException ex) {
            handleError(ex.getError(), ex.getArg(), options.getPublicUrl(),
                    request, response);
        }
    }

    private void transform(Document stubDoc, HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, TransformerException,
                ParserConfigurationException, SAXException, ProtocolException {
        Source sheet = resolve("oai.xsl", null);
        Transformer transformer = makeTransformer(sheet);
        DOMSource stub = new DOMSource(stubDoc);
        stub.setSystemId(fileInput.nextSystemId());
        transformer.transform(stub, makeAdapter(response));
    }

    private void handleError(Errors error, String errArg, String publicUrl,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, TransformerException {
        Source sheet = resolve("error.xsl", null);
        Transformer transformer = makeTransformer(sheet);

        Errors code = Errors.getCode(error);
        transformer.setParameter("errorCode", code.toString());

        String head = Builder.getErrorMessage(request.getLocale(), error, errArg);
        String errorMsg = ProtocolException.formatMessage(head, errArg);

        transformer.setParameter("errorMsg", errorMsg);
        transformer.setParameter(Builder.REQUEST_STR, publicUrl);
        transformer.transform(new DOMSource(), makeAdapter(response));
    }

    Transformer makeTransformer(Source sheet)
            throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setURIResolver(this);
        Transformer transformer = transformerFactory.newTransformer(sheet);
        transformer.setParameter("currentDateTime", DateUtil.getCurrentDateTime());
        return transformer;
    }

    private static StreamResult makeAdapter(HttpServletResponse response) throws IOException {
        return new StreamResult(response.getOutputStream());
    }

    private static void checkLink(URL sourceLink, boolean hasResumptionToken)
            throws ProtocolException, MalformedURLException {
        String path = sourceLink.getPath();
        File f = new File(path);
        if (!f.exists()) {
            LOGGER.log(Level.WARNING, "file not found: " + path);
            // non-existent source would also throw later, when trying
            // to open the file, but that's inside XSL transformation,
            // and getting custom exceptions out of resolver is
            // non-trivial...
            if (hasResumptionToken) {
                throw new ProtocolException(Errors.sourceFileNotFound, null);
            } else {
                throw new MalformedURLException("Source file not found.");
            }
        }
    }
}
