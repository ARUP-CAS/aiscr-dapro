package cz.ssc.dapro;

import cz.ssc.dapro.testrack.DocBuilder;
import cz.ssc.dapro.testrack.InternalXsltHandler;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import junit.framework.TestCase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class OaiXslTest extends TestCase {
    private final XPath xp;

    private final DocBuilder docBuilder;
    
    private Transformer transformer;

    public OaiXslTest() throws Exception {
        XPathFactory xf = XPathFactory.newInstance();
        xp = xf.newXPath();

        docBuilder = new DocBuilder();
    }

    @Override
    protected void setUp() throws Exception {
        XsltHandler xsltHandler = new InternalXsltHandler();
        Source sheet = xsltHandler.resolve("oai.xsl", null);
        assertNotNull(sheet);
        transformer = xsltHandler.makeTransformer(sheet);
        assertNotNull(transformer);
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    }
    
    @Test
    public void testListMetadataFormats() throws Exception {
        Document doc = transform("meta.xml", null);
        // the first one
        String metadataPrefix = (String)xp.evaluate("//*[local-name()='metadataPrefix']", doc, XPathConstants.STRING);
        assertEquals("oai_dc", metadataPrefix);

        doc = transform("not-found.xml", null);
        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("idDoesNotExist", code);
    }

    @Test
    public void testGetRecord() throws Exception {
        Document doc = transform("get-dc-stub.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());

        doc = transform("get-rdf-stub.xml", null);
        nodes = (NodeList)xp.evaluate("//*[local-name()='crm2rdf']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        NamedNodeMap attr = node.getAttributes();
        Node schemaLocation = attr.getNamedItemNS("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
        assertNotNull(schemaLocation);
        
        doc = transform("dc-flight.xml", null);
        nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
    }

    @Test
    public void testGetRecordDocSchema() throws Exception {
        Document doc = transform("get-amcr-stub.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
    }

    @Test
    public void testGetRecordSoubor() throws Exception {
        Document doc = transform("get-soubor.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
    }

    @Test
    public void testListRecords() throws Exception {
        Document doc = transform("records.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(2, nodes.getLength());

        doc = transform("stub.xml", null);
        nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
    }

    @Test
    public void testListRecordsExtSourceSchema() throws Exception {
        Document doc = transform("list-bib.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(5, nodes.getLength());
    }

    public void testListRecordsStart() throws Exception {
        Document doc = transform("list-start.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(5, nodes.getLength());

        String identifier = (String)xp.evaluate("//*[local-name()='identifier']/text()", doc, XPathConstants.STRING);
        assertEquals("https://api.aiscr.cz/id/C-201800019", identifier);

        nodes = (NodeList)xp.evaluate("//*[local-name()='resumptionToken']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        assertEquals("2019-05-28.oai_dc.5.projekt.5.5...f", node.getTextContent());

        doc = transform("list-next.xml", null);
        nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(5, nodes.getLength());

        nodes = (NodeList)xp.evaluate("//*[local-name()='resumptionToken']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        node = nodes.item(0);
        assertEquals("2019-05-28.oai_dc.10.projekt.10.5...f", node.getTextContent());

        identifier = (String)xp.evaluate("//*[local-name()='identifier']/text()", doc, XPathConstants.STRING);
        assertEquals("https://api.aiscr.cz/id/C-201900010", identifier);
    }

    public void testListRecordsCross() throws Exception {
        Document doc = transform("list-cross.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(5, nodes.getLength());

        nodes = (NodeList)xp.evaluate("//*[local-name()='resumptionToken']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        assertEquals("2019-05-28.oai_dc.15.akce.3.5...f", node.getTextContent());

        doc = transform("list-dc-start.xml", null);
        nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(5, nodes.getLength());

        nodes = (NodeList)xp.evaluate("//*[local-name()='resumptionToken']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        node = nodes.item(0);
        assertEquals("2019-05-28.oai_dc.5.akce.3.5...f", node.getTextContent());

        NamedNodeMap attr = node.getAttributes();
        Node attrNode = attr.getNamedItem("cursor");
        int cursor = Integer.parseInt(attrNode.getNodeValue());
        assertEquals(0, cursor);
    }

    public void testListRecordsEnd() throws Exception {
        Document doc = transform("list-end.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(2, nodes.getLength());

        nodes = (NodeList)xp.evaluate("//*[local-name()='resumptionToken']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        assertEquals("", node.getTextContent());
    }

    @Test
    public void testListRecordsRdf() throws Exception {
        Document doc = transform("records-rdf.xml", null);
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(2, nodes.getLength());
    }

    @Test
    public void testListRecordsWrongPrefix() throws Exception {
        Document doc = transform("wrong-prefix.xml", null);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("cannotDisseminateFormat", code);
    }

    private Document transform(String sourceName, String dumpName) throws IOException, SAXException, TransformerException {
        DOMSource source = new DOMSource(docBuilder.load(sourceName));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult sr = new StreamResult(baos);
        transformer.transform(source, sr);
        byte[] content = baos.toByteArray();

        if (dumpName != null) {
            try (FileOutputStream fos = new FileOutputStream(dumpName)) {
                fos.write(content);
            }
        }

        return docBuilder.buildValid(content);
    }
}
