package cz.ssc.dapro;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import cz.ssc.dapro.testrack.DocBuilder;
import cz.ssc.dapro.testrack.MockServletContext;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XsltHandlerTest extends TestCase {
    private final XPath xp;

    private final DocBuilder docBuilder;

    private final TimeZone timeZone;

    private final SimpleDateFormat simpleDateFormat;

    public XsltHandlerTest() throws Exception {
        XPathFactory xf = XPathFactory.newInstance();
        xp = xf.newXPath();

        docBuilder = new DocBuilder();

        timeZone = TimeZone.getTimeZone("UTC");
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        simpleDateFormat.setTimeZone(timeZone);
    }

    @Test
    public void testResolve() throws Exception {
        XsltHandler xsltHandler = new XsltHandler();
        Source sheet = xsltHandler.resolve("error.xsl", null);
        assertNotNull(sheet);
        Transformer transformer = xsltHandler.makeTransformer(sheet);
        assertNotNull(transformer);
    }

    // note: all tests below need not only a valid amcr-dapro.json
    // configuration file (in a directory specified by AMCR_CONFDIR
    // environment variable), but also valid export and RDF files in
    // the directory specified in the configuration's filebase
    // setting; using undated files (and setting datedsource to false
    // in the configuration) is not strictly required, but recommended
    // for ease of maintenance
    @Test
    public void testBadVerb() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        Document doc = ask(testRequest, "error-result.xml");

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("badVerb", code);
    }

    @Test
    public void testIdentify() throws Exception {
        Document doc = ask("Identify");

        MockServletContext msc = new MockServletContext();
        Options options = new Options(msc);
        String publicUrl = options.getPublicUrl();
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='baseURL']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        assertEquals(publicUrl, node.getTextContent());
    }

    @Test
    public void testListMetadataFormats() throws Exception {
        Document doc = ask("ListMetadataFormats");
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='error']", doc, XPathConstants.NODESET);
        assertEquals(0, nodes.getLength());

        // the first one
        String metadataPrefix = (String)xp.evaluate("//*[local-name()='metadataPrefix']", doc, XPathConstants.STRING);
        assertEquals("oai_dc", metadataPrefix);

        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListMetadataFormats");
        testRequest.setParameter("identifier", "https://api.aiscr.cz/id/X-BIB-0709996");
        doc = ask(testRequest);

        nodes = (NodeList)xp.evaluate("//*[local-name()='metadataPrefix']", doc, XPathConstants.NODESET);
        assertEquals(2, nodes.getLength());

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListMetadataFormats");
        testRequest.setParameter("identifier", "https://api.aiscr.cz/id/C-DL-200900001");
        doc = ask(testRequest);

        nodes = (NodeList)xp.evaluate("//*[local-name()='metadataPrefix']", doc, XPathConstants.NODESET);
        assertEquals(3, nodes.getLength());
    }

    @Test
    public void testListMetadataFormatsWrongIdentifier() throws Exception {
        MockServletContext msc = new MockServletContext();
        Options options = new Options(msc);
        String identifier = options.getIdentifierHead() + "A-BC-123456789";

        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListMetadataFormats");
        testRequest.setParameter("identifier", identifier);
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("idDoesNotExist", code);
    }

    @Test
    public void testListRecordsNoParams() throws Exception {
        Document doc = ask("ListRecords");

        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='error'][@code='badArgument']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        assertEquals("Missing argument: metadataPrefix", node.getTextContent());
    }

    @Test
    public void testListRecords() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        Document doc = ask(testRequest);

        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='datestamp']", doc, XPathConstants.NODESET);
        assertTrue(nodes.getLength() > 0);

        nodes = (NodeList)xp.evaluate("//*[local-name()='metadata']", doc, XPathConstants.NODESET);
        int pageSize = nodes.getLength();
        assertTrue(pageSize > 0);
    }

    @Test
    public void testListRecordsProjDate() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("from", "1970-01-01");
        Document doc = ask(testRequest);

        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='metadata']", doc, XPathConstants.NODESET);
        int pageSize = nodes.getLength();
        assertTrue(pageSize > 0);

        nodes = (NodeList)xp.evaluate("//*[local-name()='resumptionToken']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        Node node = nodes.item(0);
        NamedNodeMap attr = node.getAttributes();
        Node attrNode = attr.getNamedItem("cursor");
        int cursor = Integer.parseInt(attrNode.getNodeValue());
        assertEquals(0, cursor);

        String resumptionToken = node.getTextContent();
        String tail = String.format(".oai_amcr.%1$d.projekt.%1$d.%1$d.1970-01-01..f", pageSize);
        assertTrue(resumptionToken.endsWith(tail));

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("resumptionToken", resumptionToken);
        doc = ask(testRequest, null);
        nodes = (NodeList)xp.evaluate("//*[local-name()='resumptionToken']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        node = nodes.item(0);
        attr = node.getAttributes();
        attrNode = attr.getNamedItem("cursor");
        cursor = Integer.parseInt(attrNode.getNodeValue());
        assertEquals(pageSize, cursor);
    }

        /*MockServletContext msc = new MockServletContext();
        Options options = new Options(msc);
        int configPageSize = options.getPageRows();
        int lastPageSize = completeListSize % configPageSize;
        if (lastPageSize == 0) {
            lastPageSize = configPageSize;
        }

        String resumptionToken = String.format("%d,%d,%s",
                completeListSize - lastPageSize, configPageSize, "oai_dc");
        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("resumptionToken", resumptionToken);
        doc = ask(testRequest);
        nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        pageSize = nodes.getLength();
        assertEquals(lastPageSize, pageSize);
        String emptyResumptionToken = (String)xp.evaluate("//*[local-name()='resumptionToken']/text()",
                doc, XPathConstants.STRING);
        assertEquals("", emptyResumptionToken);

        Date fromDate = firstDate.getTime();
        String fromStr = formatDate(fromDate);
        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("from", fromStr);
        doc = ask(testRequest);
        nodes = (NodeList)xp.evaluate("//*[local-name()='datestamp']", doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); ++i) {
            node = nodes.item(i);
            Calendar futureDate = DatatypeConverter.parseDate(node.getTextContent());
            assertTrue(futureDate.getTimeInMillis() >= fromDate.getTime());
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        dayFormat.setTimeZone(timeZone);
        fromStr = dayFormat.format(fromDate);
        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("from", fromStr);
        ask(testRequest);

        Calendar futureDate = Calendar.getInstance();
        futureDate.add(Calendar.HOUR, 24);
        String untilStr = dayFormat.format(futureDate.getTime());
        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("until", untilStr);
        ask(testRequest);*/

    @Test
    public void testListRecordsWrongDate() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("from", "2019-05-31T11:44:15Z");
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("badArgument", code);
    }

    @Test
    public void testListRecordsSet() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "soubor");
        Document doc = ask(testRequest, null);

        Node node = (Node)xp.evaluate("//*[local-name()='request']", doc, XPathConstants.NODE);
        NamedNodeMap attr = node.getAttributes();
        Node attrNode = attr.getNamedItem("set");
        assertEquals("soubor", attrNode.getNodeValue());

        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='identifier']/text()", doc, XPathConstants.NODESET);
        int len = nodes.getLength();
        assertTrue(len > 0);
        HashSet<String> identifiers = new HashSet<>();
        for (int i = 0; i < len; ++i) {
            node = nodes.item(i);
            String ident = node.getNodeValue();
            identifiers.add(ident);
        }

        assertEquals(len, identifiers.size());
    }

    @Ignore // requires full set of source XML files
    public void testListRecordsSetSchema() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "akce");
        ask(testRequest);

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "lokalita");
        ask(testRequest);

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "let");
        ask(testRequest);

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "dok_jednotka");
        ask(testRequest);

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "dokument");
        ask(testRequest);

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "ext_zdroj");
        ask(testRequest);

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "pian");
        ask(testRequest);
    }

    @Test
    public void testListRecordsWrongSet() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_amcr");
        testRequest.setParameter("set", "x");
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("badArgument", code);
    }

    @Test
    public void testListRecordsProjRdfDate() throws Exception {
        final String minDate = "2000-01-01";

        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_rdf");
        testRequest.setParameter("from", minDate);
        testRequest.setParameter("until", "2019-05-24");
        Document doc = ask(testRequest, null);
        String foundDate = getMinDate(doc);
        assertTrue(foundDate.compareTo(minDate) >= 0);
    }

    @Test
    public void testListRecordsDocDate() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        Document doc = ask(testRequest, null);

        String midDate = getMedianDate(doc);
        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("until", midDate);
        doc = ask(testRequest, null);
        String maxDate = getMaxDate(doc);
        assertTrue(maxDate.compareTo(midDate) <= 0);
    }

    @Test
    public void testListRecordsWrongResumptionToken() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("resumptionToken", "x");
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("badResumptionToken", code);
    }

    @Test
    public void testListRecordsWrongPrefix() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("set", "projekt");
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("cannotDisseminateFormat", code);

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "!@#$%^");
        doc = ask(testRequest);

        code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("badArgument", code);
    }
    
    @Test
    public void testListRecordsWrongFrom() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("from", "little Bobby Tables");
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("badArgument", code);
    }

    @Test
    public void testListRecordsNoResult() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dayFormat = DateUtil.initDayFormat();
        String fromStr = dayFormat.format(cal.getTime());

        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("from", fromStr);
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("noRecordsMatch", code);
    }

    /*@Test
    public void testListIdentifiers() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListIdentifiers");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        Document doc = ask(testRequest);

        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='record']", doc, XPathConstants.NODESET);
        assertEquals(0, nodes.getLength());

        nodes = (NodeList)xp.evaluate("//*[local-name()='header']", doc, XPathConstants.NODESET);
        int pageSize = nodes.getLength();
        assertTrue(pageSize > 0);

        nodes = (NodeList)xp.evaluate("//*[local-name()='datestamp']", doc, XPathConstants.NODESET);
        assertTrue(nodes.getLength() > 0);
        Node node = nodes.item(0);
        Calendar firstDate = DatatypeConverter.parseDate(node.getTextContent());
        firstDate.add(Calendar.HOUR, 1);

        nodes = (NodeList)xp.evaluate("//*[local-name()='resumptionToken']", doc, XPathConstants.NODESET);
        assertEquals(1, nodes.getLength());
        node = nodes.item(0);

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListIdentifiers");
        testRequest.setParameter("resumptionToken", node.getTextContent());
        doc = ask(testRequest);
        String cursor = (String)xp.evaluate("//*[local-name()='resumptionToken']/@cursor", doc, XPathConstants.STRING);
        assertEquals(Integer.toString(pageSize), cursor);

        Date untilDate = firstDate.getTime();
        String untilStr = formatDate(untilDate);
        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListIdentifiers");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("until", untilStr);
        doc = ask(testRequest);
        nodes = (NodeList)xp.evaluate("//*[local-name()='datestamp']", doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); ++i) {
            node = nodes.item(i);
            Calendar pastDate = DatatypeConverter.parseDate(node.getTextContent());
            assertTrue(pastDate.getTimeInMillis() <= untilDate.getTime());
        }
    }*/

    @Test
    public void testListIdentifiersNoResult() throws Exception {
        String untilStr = "1000-01-01";

        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListIdentifiers");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        testRequest.setParameter("until", untilStr);
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("noRecordsMatch", code);
    }

    @Test
    public void testGetRecord() throws Exception {
        // get a valid identifier
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "ListRecords");
        testRequest.setParameter("metadataPrefix", "oai_dc");
        Document doc = ask(testRequest);

        // finds nodes in multiple namespaces but with the same content
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='identifier']", doc, XPathConstants.NODESET);
        assertTrue(nodes.getLength() > 0);
        Node node = nodes.item(0);
        String identifier = node.getTextContent();

        testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "GetRecord");
        testRequest.setParameter("identifier", identifier);
        testRequest.setParameter("metadataPrefix", "oai_dc");
        ask(testRequest);
    }

    @Test
    public void testGetRecordWrongIdentifier() throws Exception {
        MockServletContext msc = new MockServletContext();
        Options options = new Options(msc);
        String identifier = options.getIdentifierHead() + "A-BC-123456789";

        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "GetRecord");
        testRequest.setParameter("identifier", identifier);
        testRequest.setParameter("metadataPrefix", "oai_dc");
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("idDoesNotExist", code);
    }

    @Test
    public void testGetRecordExtraArgument() throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", "GetRecord");
        testRequest.setParameter("identifier", "https://api.aiscr.cz/id/X-BIB-0710038-part3");
        testRequest.setParameter("metadataPrefix", "oai_rdf");
        testRequest.setParameter("set", "ext_zdroj");
        Document doc = ask(testRequest);

        String code = (String)xp.evaluate("//*[local-name()='error']/@code", doc, XPathConstants.STRING);
        assertEquals("badArgument", code);
    }

    @Test
    public void testListSets() throws Exception {
        Document doc = ask("ListSets");
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='setSpec']", doc, XPathConstants.NODESET);
        assertEquals(10, nodes.getLength());
    }

    private Document ask(String verb) throws Exception {
        GetMethodWebRequest testRequest = new GetMethodWebRequest("http://example.com/dapro/oai");
        testRequest.setParameter("verb", verb);
        return ask(testRequest);
    }

    private Document ask(WebRequest testRequest) throws Exception {
        return ask(testRequest, null);
    }

    private Document ask(WebRequest testRequest, String dumpName) throws Exception {
        ServletRunner sr = new ServletRunner();
        ServletUnitClient sc = sr.newClient();
        InvocationContext ic = sc.newInvocation(testRequest);
        HttpServletRequest request = ic.getRequest();
        HttpServletResponse response = ic.getResponse();

        MockServletContext msc = new MockServletContext();
        Options options = new Options(msc);
        XsltHandler xsltHandler = new XsltHandler();
        xsltHandler.process(request, response, options);

        // assertEquals(200, response.getStatus()); don't test; not set
        assertEquals("text/xml", response.getContentType());

        Method getter = response.getClass().getDeclaredMethod("getContents");
        getter.setAccessible(true);
        Object raw = getter.invoke(response);
        byte[] content = (byte[])raw;
        if (dumpName != null) {
            try (FileOutputStream fos = new FileOutputStream(dumpName)) {
                fos.write(content);
            }
        }

        Document doc = docBuilder.buildValid(content);
        String responseDate = (String)xp.evaluate("//*[local-name()='responseDate']/text()", doc, XPathConstants.STRING);
        DatatypeConverter.parseDate(responseDate);
        return doc;
    }

    private String getMedianDate(Document doc) throws XPathExpressionException {
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='datestamp']", doc, XPathConstants.NODESET);
        int len = nodes.getLength();
        assertTrue(len > 0);
        ArrayList<String> dates = new ArrayList<>();
        for (int i = 0; i < len; ++i) {
            Node node = nodes.item(i);
            String d = node.getTextContent();
            assertNotNull(d);
            dates.add(d);
        }

        Collections.sort(dates);
        return dates.get(len / 2);
    }

    private String getMinDate(Document doc) throws XPathExpressionException {
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='datestamp']", doc, XPathConstants.NODESET);
        int len = nodes.getLength();
        assertTrue(len > 0);
        String minDate = null;
        for (int i = 0; i < len; ++i) {
            Node node = nodes.item(i);
            String d = node.getTextContent();
            assertNotNull(d);
            if ((minDate == null) || (d.compareTo(minDate) < 0)) {
                minDate = d;
            }
        }

        return minDate;
    }

    private String getMaxDate(Document doc) throws XPathExpressionException {
        NodeList nodes = (NodeList)xp.evaluate("//*[local-name()='datestamp']", doc, XPathConstants.NODESET);
        int len = nodes.getLength();
        assertTrue(len > 0);
        String maxDate = null;
        for (int i = 0; i < len; ++i) {
            Node node = nodes.item(i);
            String d = node.getTextContent();
            assertNotNull(d);
            if ((maxDate == null) || (d.compareTo(maxDate) > 0)) {
                maxDate = d;
            }
        }

        return maxDate;
    }
}
