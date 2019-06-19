package cz.ssc.dapro;

import cz.ssc.dapro.testrack.MockServletContext;
import junit.framework.TestCase;
import org.junit.Test;

public class OptionsTest extends TestCase {
    // note: this test needs a valid amcr-dapro.json configuration
    // file, in a directory specified by AMCR_CONFDIR environment
    // variable
    @Test
    public void testGetSolrDefaultHandler() throws Exception {
        MockServletContext msc = new MockServletContext();
        Options options = new Options(msc);
        String fileBase = options.getFileBase();
        assertTrue(fileBase.startsWith("file:///"));
    }
}
