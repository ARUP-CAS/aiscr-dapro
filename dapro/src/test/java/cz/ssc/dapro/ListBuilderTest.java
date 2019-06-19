package cz.ssc.dapro;

import cz.ssc.dapro.testrack.MockServletContext;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ListBuilderTest extends TestCase {
    private final DocumentBuilder documentBuilder;

    public ListBuilderTest() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        documentBuilder = dbf.newDocumentBuilder();
    }

    @Test
    public void testProcessResumptionToken() throws ProtocolException, IOException {
        MockServletContext msc = new MockServletContext();
        Options options = new Options(msc);

        String[] tokens = { "2019-05-28.oai_dc.2.projekt.2.2...t",
            "2019-05-28.oai_dc.100.akce.10.10.2019-05-23..f"};
        for (int i = 0; i < tokens.length; ++i) {
            checkValid(options, tokens[i]);
        }

        String[] invalidTokens = { "2,2,oai_dc,,", "x/1/y/2///3",
            "oai_dc/100/event/10/10",
            "28May2019.oai_dc.100.akce.10.10.2019-05-23..f" };
        for (int i = 0; i < invalidTokens.length; ++i) {
            checkInvalid(options, invalidTokens[i]);
        }
    }

    private void checkValid(Options options, String token) throws ProtocolException {
        ListBuilder lb = new ListBuilder();
        Document doc = documentBuilder.newDocument();
        Element stub = doc.createElement("stub");
        doc.appendChild(stub);
        lb.processResumptionToken(stub, options, token);

        int cnt = 0;
        Node child = stub.getFirstChild();
        while (child != null) {
            cnt++;
            child = child.getNextSibling();
        }

        assertTrue(cnt >= 4);
    }

    private void checkInvalid(Options options, String token) {
        ListBuilder lb = new ListBuilder();
        Document doc = documentBuilder.newDocument();
        Element stub = doc.createElement("stub");
        doc.appendChild(stub);
        try {
            lb.processResumptionToken(stub, options, token);
            fail("no exception thrown");
        } catch (ProtocolException ex) {
            assertEquals(Errors.badResumptionToken, ex.getError());
        }
    }
}
