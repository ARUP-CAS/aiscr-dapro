package cz.ssc.dapro.testrack;

import cz.ssc.dapro.BulkFilter;
import cz.ssc.dapro.FilterSpec;
import cz.ssc.dapro.ProtocolException;
import cz.ssc.dapro.XsltHandler;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.apache.http.client.utils.URIBuilder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class InternalXsltHandler extends XsltHandler {
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        if (href.startsWith("res:")) {
            try {
                // URIBuilder doesn't recognize parameters of relative URLs
                URIBuilder builder = new URIBuilder("http://example.com/" + href.substring(4));
                FilterSpec spec = new FilterSpec(builder);
                int pos = href.indexOf("?");
                String relName = (pos > 0) ? href.substring(4, pos) : href.substring(4);
                String resourceName = "/data/" + relName;
                URL xu = getClass().getResource(resourceName);
                return filter(xu.openStream(), spec);
            } catch (IOException | InterruptedException |
                    ProtocolException | SAXException |
                    TransformerConfigurationException | URISyntaxException ex) {
                throw new TransformerException(ex);
            }
        } else {
            return super.resolve(href, base);
        }
    }

    Source filter(InputStream stream, FilterSpec spec) 
            throws TransformerConfigurationException, IOException, 
                InterruptedException, SAXException {
        if (stream == null) {
            throw new IllegalArgumentException();
        }

        if (spec == null) {
            throw new IllegalArgumentException();
        }

        SAXTransformerFactory factory = (SAXTransformerFactory)(TransformerFactory.newInstance());
        TransformerHandler serializer = factory.newTransformerHandler();
        DOMResult result = new DOMResult();
        serializer.setResult(result);
        /*StreamResult testResult = new StreamResult(new File("x.xml"));
        serializer.setResult(testResult);*/

        BulkFilter filter = new BulkFilter(spec);
        filter.setContentHandler(serializer);

        XMLReader xmlreader = XMLReaderFactory.createXMLReader();
        xmlreader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        xmlreader.setContentHandler(filter);
        xmlreader.parse(new InputSource(stream));

        return new DOMSource(result.getNode());
    }
}
