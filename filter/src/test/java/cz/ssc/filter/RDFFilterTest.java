package cz.ssc.filter;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Juraj
 */
public class RDFFilterTest {

    public static final String EXPORT_FILES_DIR = "src/test/resources/exports/";
    
    @Test 
    public void testCorrectFilteringProject() throws Exception {
        
        Document doc = getDocument("export_project2.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(Constants.PROJECT);
        
        // There are two project nodes in the xml
        assertEquals(nList.getLength(), 2);
        
        // First project is archived therefore it is accessible by Anonym
        assertTrue(RDFFilter.filter(nList.item(0), AccessLevel.ANONYM));
        // Second project is not archived therefore it should not be accessible
        assertFalse(RDFFilter.filter(nList.item(1), AccessLevel.ANONYM));
        // Archeolog has access even to second project
        assertTrue(RDFFilter.filter(nList.item(1), AccessLevel.ARCHEOLOG));
        
    }
    
    @Test 
    public void testCorrectFilteringAkce() throws Exception {
        
        Document doc = getDocument("export_akce4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(Constants.EVENT);
        
        assertEquals(nList.getLength(), 4);
        
        // State 6, lvl A
        assertFalse(RDFFilter.filter(nList.item(0), AccessLevel.ANONYM));
        // State 8, lvl A
        assertTrue(RDFFilter.filter(nList.item(1), AccessLevel.ANONYM));
        // State 1, lvl A
        assertTrue(RDFFilter.filter(nList.item(2), AccessLevel.ARCHEOLOG));
        
    }
    
    @Test 
    public void testCorrectFilteringLokalita() throws Exception {
        
        Document doc = getDocument("export_lokalita4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(Constants.SITE);
        
        assertEquals(nList.getLength(), 4);
        
        // State 3, lvl A
        assertTrue(RDFFilter.filter(nList.item(0), AccessLevel.ANONYM));
        // State 1, lvl C
        assertFalse(RDFFilter.filter(nList.item(1), AccessLevel.ANONYM));
        // State 2, lvl D
        assertFalse(RDFFilter.filter(nList.item(2), AccessLevel.ARCHEOLOG));
        
    }
    
    @Test 
    public void testCorrectFilteringDokument() throws Exception {
        
        Document doc = getDocument("export_dokument4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(Constants.DOCUMENT);
        
        assertEquals(nList.getLength(), 4);
       
        // State 3
        assertTrue(RDFFilter.filter(nList.item(0), AccessLevel.ANONYM));
        // State 1
        assertFalse(RDFFilter.filter(nList.item(1), AccessLevel.ANONYM));
        // State 3
        assertTrue(RDFFilter.filter(nList.item(0), AccessLevel.ARCHEOLOG));
        
    }
    
    @Test 
    public void testCorrectFilteringPIAN() throws Exception {
        
        Document doc = getDocument("export_PIAN3.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(Constants.PIAN);
        
        assertEquals(nList.getLength(), 3);
        
        // State N, lvl A
        assertFalse(RDFFilter.filter(nList.item(0), AccessLevel.ANONYM));
        // State N, lvl A
        assertTrue(RDFFilter.filter(nList.item(0), AccessLevel.ADMIN));
        // State P, lvl A
        assertTrue(RDFFilter.filter(nList.item(1), AccessLevel.ANONYM));
        
    }
    
    @Test 
    public void testCorrectFilteringDocJednotka() throws Exception {
        
        Document doc = getDocument("export_doc_jed4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("dok_jednotka");
        
        assertEquals(nList.getLength(), 4);
        
        // site state 3, lvl B
        assertFalse(RDFFilter.filter(nList.item(0), AccessLevel.ANONYM));
        // site state 3, lvl B
        assertTrue(RDFFilter.filter(nList.item(0), AccessLevel.BADATEL));
        // event state 3, lvl B
        assertFalse(RDFFilter.filter(nList.item(1), AccessLevel.BADATEL));
        // event state 3, lvl B
        assertTrue(RDFFilter.filter(nList.item(1), AccessLevel.ARCHEOLOG));
        
    }
    
    @Test 
    public void testCorrectFilteringLet() throws Exception {
        
        Document doc = getDocument("export_let4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("let");
        
        assertEquals(nList.getLength(), 4);
        
        // Filtering is not done, everyone can see everything
        for(int i = 0; i < nList.getLength(); i++){
            for(AccessLevel lvl: AccessLevel.values()){
                assertTrue(RDFFilter.filter(nList.item(i), lvl));
            }
        }
    }

    @Test(expected = TagNotFoundException.class)
    public void testPristupnostTagMissingException() throws Exception {
        Document doc = getDocument("export_PIAN3.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("PIAN");

        // Check that correct behavior when tag pristupnost is missing
        RDFFilter.filter(nList.item(2), AccessLevel.ADMIN);

    }
    
    @Test(expected = TagNotFoundException.class)
    public void testPristupnostCorrectValueException() throws Exception {
        Document doc = getDocument("export_doc_jed4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("dok_jednotka");

        // Check that correct behavior when tag pristupnost has value different from A-E
        RDFFilter.filter(nList.item(3), AccessLevel.ADMIN);

    }
    
    @Test(expected = TagNotFoundException.class)
    public void testStavTagMissingException() throws Exception {
        Document doc = getDocument("export_akce4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("akce");

        // Check that correct behavior when tag stav is missing
        RDFFilter.filter(nList.item(3), AccessLevel.ADMIN);

    }
    
    @Test(expected = TagNotFoundException.class)
    public void testTooManyStavTagsException() throws Exception {
        Document doc = getDocument("export_dokument4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("dokument");

        // Check that correct behavior when tag stav is there twice
        RDFFilter.filter(nList.item(3), AccessLevel.ADMIN);

    }
    
    @Test(expected = TagNotFoundException.class)
    public void testEmptyStavTagsException() throws Exception {
        Document doc = getDocument("export_lokalita4.xml");
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("lokalita");

        // Check that correct behavior when tag stav is empty
        RDFFilter.filter(nList.item(3), AccessLevel.ADMIN);

    }
    
    @Test(expected = TagNotFoundException.class)
    public void testNullNodeException() throws Exception {

        // Check that correct behavior when node is null
        RDFFilter.filter(null, AccessLevel.ADMIN);

    }
    
    private Document getDocument(String fileName) throws ParserConfigurationException, SAXException, IOException{
        File xmlFile = new File(EXPORT_FILES_DIR+fileName);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = factory.newDocumentBuilder();
        return dBuilder.parse(xmlFile);
    }
    
    
}
