package cz.ssc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 *
 * @author Juraj
 */
public class RDFConTest {

    public static final String MAPPING_FILES_DIR = "src/test/resources/mapping_files/";
    public static final String EXPORT_FILES_DIR = "src/test/resources/exports/";
    public static final String GENERATOR_POL_FILE = "src/test/resources/AMCR_Generator_Policy.xml";

    @Test
    public void testIncorrectKeyword() {
        try {
            RDFCon.checkParametersOK("mapping_file_projekt.xml", "projekk");
            assertTrue(false);
        } catch (Exception ex) { }
    }

    @Test
    public void testNoKeywordInMappingFilePath() {
        try {
            RDFCon.checkParametersOK("mapping_file_let.xml", "projekt");
            assertTrue(false);
        } catch (Exception ex) { }
    }
    
    // Long test (cca 400s)
//   @Test
//    public void testConversionSuccessProjectFull() {
//
//        String output_file = "rdf_projekt_full.rdf";
//
//        try {
//            RDFCon.go(
//                    EXPORT_FILES_DIR + "export_2019-05-15_projekt_amcr.xml",
//                    MAPPING_FILES_DIR + "AMCR_to_CIDOC_X3ML_190319_projekt.xml",
//                    GENERATOR_POL_FILE,
//                    output_file,
//                    "projekt",
//                    false);
//
//            Path path = Paths.get(output_file);
//            // The file exists
//            assertTrue(Files.exists(path));
//            if (Files.exists(path)) {
//                // Its size is non zero
//                assertTrue(Files.size(path) != 0);
//                Files.delete(path);
//            }
//        } catch (Exception ex) {
//            assertEquals(true, false);
//        }
//    }

    @Test
    public void testConversionSuccessProject() {

        String output_file = "rdf_projekt.rdf";

        try {
            RDFCon.go(
                    EXPORT_FILES_DIR + "export_project2.xml",
                    MAPPING_FILES_DIR + "AMCR_to_CIDOC_X3ML_projekt.xml",
                    GENERATOR_POL_FILE,
                    output_file,
                    "projekt",
                    false);

            Path path = Paths.get(output_file);
            // The file exists
            assertTrue(Files.exists(path));
            if (Files.exists(path)) {
                // Its size is non zero
                assertTrue(Files.size(path) != 0);
                Files.delete(path);
            }
        } catch (Exception ex) {
            assertEquals(true, false);
        }
    }

    @Test
    public void testOrderOfExportedNodes() {

        String output_file1 = "rdf_event1.rdf";
        String output_file2 = "rdf_event2.rdf";

        try {
            RDFCon.go(
                    EXPORT_FILES_DIR + "export_akce4.xml",
                    MAPPING_FILES_DIR + "AMCR_to_CIDOC_X3ML_akce.xml",
                    GENERATOR_POL_FILE,
                    output_file1,
                    "akce",
                    false);

            File first = new File(output_file1);

            RDFCon.go(
                    EXPORT_FILES_DIR + "export_akce4.xml",
                    MAPPING_FILES_DIR + "AMCR_to_CIDOC_X3ML_akce.xml",
                    GENERATOR_POL_FILE,
                    output_file2,
                    "akce",
                    false);
            
            File second = new File(output_file2);

            checkFilesEqual(first.getAbsolutePath(), second.getAbsolutePath());
            
            Files.delete(Paths.get(output_file1));
            Files.delete(Paths.get(output_file2));
            
        } catch (Exception ex) {
            assertEquals(true, false);
        }
    }
    
    private void checkFilesEqual(String first, String second) throws IOException{
        BufferedReader reader1 = new BufferedReader(new FileReader(first));
        BufferedReader reader2 = new BufferedReader(new FileReader(second));
         
        String line1 = reader1.readLine();
        String line2 = reader2.readLine();
         
        boolean areEqual = true;
        int lineNum = 1;
         
        while (line1 != null || line2 != null)
        {
            if(line1 == null || line2 == null)
            {
                areEqual = false;
                break;
            }
            else if(! line1.equalsIgnoreCase(line2))
            {
                areEqual = false;
                break;
            }
             
            line1 = reader1.readLine();
            line2 = reader2.readLine();
            lineNum++;
        }
        
        reader1.close();
        reader2.close();
        
        assertTrue(areEqual);
    
    }
}
