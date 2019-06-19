package cz.ssc.dapro;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import cz.ssc.dapro.testrack.MockServletContext;
import cz.ssc.filter.AccessLevel;
import java.io.File;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BuilderTest extends TestCase {
    private final XPath xp;

    private final Transformer transformer;

    public BuilderTest() throws Exception {
        XPathFactory xf = XPathFactory.newInstance();
        xp = xf.newXPath();

        TransformerFactory tf = TransformerFactory.newInstance();
        transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }

    // note: all tests below need a valid amcr-dapro.json
    // configuration file, in a directory specified by AMCR_CONFDIR
    // environment variable
    @Test
    public void testMakeStubXml() throws Exception {
        Document doc = build(null);
        NodeList nodes = (NodeList)xp.evaluate("//str[@name='verb']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
    }

    private Document build(String dumpName) throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "Identify");

        ServletRunner sr = new ServletRunner();
        ServletUnitClient sc = sr.newClient();
        InvocationContext ic = sc.newInvocation(testRequest);
        HttpServletRequest request = ic.getRequest();
        MockServletContext msc = new MockServletContext();
        Options options = new Options(msc);

        Builder builder = new Builder();
        AccessLevel lvl = AccessLevel.ANONYM;
        Document stubDoc = builder.makeStubXml(request, options, lvl);
        if (dumpName != null) {
            Source input = new DOMSource(stubDoc);
            Result output = new StreamResult(new File(dumpName));
            transformer.transform(input, output);
        }

        Element params = Builder.getParams(stubDoc);
        assertEquals("lst", params.getNodeName());

        return stubDoc;
    }
}
