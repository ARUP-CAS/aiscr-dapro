package cz.ssc.dapro;

import cz.ssc.filter.AccessLevel;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.client.utils.URIBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * Transformation specification ("stub XML") builder.
 */
public class Builder {
    static final String VERB = "verb";

    // Input format is specified to have a date for every record, but
    // the date is the most recent from several elements, so that its
    // existence is difficult to ensure (and not checked). The
    // datestamp parameter has a backstop default value.
    static final String DATESTAMP = "datestamp";

    static final String DATESTAMP_VALUE = "1990-01-01"; // same as in Identify

    static final String EXPIRATION_DATETIME = "expirationDateTime";

    static final String FROM = "from";

    static final String IDENTIFIER = "identifier";

    static final String IDENTIFIER_HEAD = "identifierHead";

    static final String METADATA_PREFIX = "metadataPrefix";

    static final String OFFSET = "offset";

    static final String QUERY = "q";

    static final String REQUEST_STR = "requestStr";

    static final String RESUMPTION_TOKEN = "resumptionToken";

    static final String TOKEN_DAY = "tokenDay";

    static final String ROWS = "rows";

    static final String SCHEMA_HEAD = "schemaHead";

    static final String SET = "set";

    static final String SET_SCOPE = "setScope";

    static final String START = "start";

    static final String UNTIL = "until";

    static final String USER_GROUP = "userGroup";

    public boolean useSourceUrl() {
        return true;
    }

    public int getRowLimit(Options options) {
        return options.getPageRows();
    }

    public URL makeSourceUrl(RequestData requestData, Options options,
            String entityName, int offset, AccessLevel level)
            throws MalformedURLException, URISyntaxException {
        URIBuilder builder = new URIBuilder(makeHead(options,
                requestData.getExportDate(),
                requestData.hasInternalFormat(),
                entityName,
                level));
        String identCely = requestData.getIdentCely();
        if (identCely != null) {
            builder.addParameter(Builder.IDENTIFIER, identCely);
        }

        if (offset > 0) {
            builder.addParameter(OFFSET, Integer.toString(offset));
        }

        int limit = Math.min(getRowLimit(options), requestData.getLimit());
        builder.addParameter(ROWS, Integer.toString(limit));

        String from = requestData.getFrom();
        if (from != null) {
            builder.addParameter(FROM, from);
        }

        String until = requestData.getUntil();
        if (until != null) {
            builder.addParameter(UNTIL, until);
        }

        URI uri = builder.build();
        return uri.toURL();
    }

    public Document makeStubXml(HttpServletRequest request, Options options, AccessLevel lvl)
            throws ProtocolException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        Element stub = doc.createElement("stub");
        Element lst = doc.createElement("lst");
        lst.setAttribute("name", "params");
        stub.appendChild(lst);
        doc.appendChild(stub);
        addParameters(request, options, lst, lvl);
        return doc;
    }

    public void addParameters(HttpServletRequest request, Options options,
            Element params, AccessLevel lvl) throws ProtocolException {
        appendParamNode(params, VERB, request.getParameter(VERB));
        appendParamNode(params, REQUEST_STR, options.getPublicUrl());
        appendParamNode(params, SCHEMA_HEAD, options.getSchemaHead());

        // params should probably have all error messages, but these were found and tested
        appendErrorMessage(params, request, Errors.idDoesNotExist);
        appendErrorMessage(params, request, Errors.cannotDisseminateFormat);
        appendErrorMessage(params, request, Errors.noRecordsMatch);
    }

    public static Element getParams(Document stubDoc) {
        Element stub = stubDoc.getDocumentElement();
        return (Element)(stub.getFirstChild());
    }

    public static Element appendParamNode(Element params, String name,
            String value) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }

        if ("link".equals(name)) { // links are special - see the method below
            throw new IllegalArgumentException("link");
        }

        if (value == null) {
            throw new IllegalArgumentException("value");
        }

        Document doc = params.getOwnerDocument();
        Element str = doc.createElement("str");
        str.setAttribute("name", name);
        Text cnt = doc.createTextNode(value);
        str.appendChild(cnt);
        params.appendChild(str);
        return str;
    }

    public static Element appendIntParam(Element params, String name,
            int value) {
        return appendParamNode(params, name, Integer.toString(value));
    }

    public static Element appendLink(Element params, String recordClass, String value) {
        Document doc = params.getOwnerDocument();
        Element str = doc.createElement("str");
        str.setAttribute("name", "link");
        str.setAttribute("class", recordClass);
        Text cnt = doc.createTextNode(value);
        str.appendChild(cnt);
        params.appendChild(str);
        return str;
    }

    public static void ensureValid(String metadataPrefix) throws ProtocolException {
        if (metadataPrefix == null) {
            throw new ProtocolException(Errors.badArgument, METADATA_PREFIX);
        }

        if (!ResumptionToken.isValidPrefix(metadataPrefix)) {
            throw new ProtocolException(Errors.illegalArgumentValue, METADATA_PREFIX);
        }
    }

    public static String getErrorMessage(Locale locale, Errors error, String errArg) {
        ResourceBundle errors = ResourceBundle.getBundle("i18n.errors", locale);
        String name = error.toString();
        if (errArg != null) {
            name += "1";
        }

        return errors.getString("error." + name);
    }

    static void appendErrorMessage(Element params,
            HttpServletRequest request, Errors error) {
        String msg = getErrorMessage(request.getLocale(), error, null);
        appendParamNode(params, error.toString(), msg);
    }

    static String makeHead(Options options, String exportDate, boolean internal,
            String entityName, AccessLevel level) {
        StringBuilder sb = new StringBuilder();
        sb.append(options.getFileBase());

        if (internal) {
            sb.append("export");
        } else {
            sb.append("rdf");
        }

        sb.append('_');

        if (options.getDatedSource()) {
            sb.append(exportDate);
            sb.append('_');
        }

        sb.append(entityName);
        sb.append('_');

        if (!internal || (level == AccessLevel.ADMIN)) {
            sb.append("amcr");
        } else {
            sb.append(level.getKey());
        }

        sb.append(".xml");
        return sb.toString();
    }
}
