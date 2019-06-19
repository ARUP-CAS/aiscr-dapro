package cz.ssc.dapro;

import org.apache.tomcat.util.codec.binary.Base64;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * note: some test are dependent on working apiary mocked API endpoints.
 * @author Juraj
 */
public class AuthenticatorTest extends TestCase {
    
    public static final String SOME_URL = "http://amcr-pass.cz";
    
    /**
     * Situation when no header is included in the request.
     * @throws Exception 
     */
    @Test
    public void testGetUserAccessNoHeader() throws Exception{
    
        String userAccess = Authenticator.getUserAccess(null, SOME_URL);
        // Anonymous user access level
        assertEquals(userAccess, "A");
    }
    
    /**
     * Incorrect header format.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetUserAccessNotBasic() throws Exception {
        try {
            Authenticator.getUserAccess("BASI USER:PASS", SOME_URL);
            assertTrue(false);
        } catch (BadAuthException ex) {
            assertEquals(ex.getMessage(), "Incorrect authentication method.");
        }
    }
    
    @Test
    public void testGetUserAccessMissingColon() throws Exception {
        try {
            Authenticator.getUserAccess("BASIC USER;PASS", SOME_URL);
            assertTrue(false);
        } catch (BadAuthException ex) {
            assertEquals(ex.getMessage(), "BASIC AUTH missing colon.");
        }
    }

    @Test
    public void testGetUserAccessCorrectPass() throws Exception{
        
        String mockUrl = "https://private-48ccba-amcrpass.apiary-mock.com/checkCorrect";
        
        String unPasDec = "juraj.skvarla@spacesystems.cz:corr:\"ectPass";
        String unPasEnc = Base64.encodeBase64String(unPasDec.getBytes());
        
        String userAccess = Authenticator.getUserAccess("BASIC "+unPasEnc, mockUrl);
       
        assertEquals(userAccess, "E");
    }

    @Test
    public void testGetUserAccessIncorrectPass() throws Exception {
        String mockUrl = "https://private-48ccba-amcrpass.apiary-mock.com/checkIncorrect";
        String unPasDec = "juraj.skvarla@spacesystems.cz:incorrectPass";
        String unPasEnc = Base64.encodeBase64String(unPasDec.getBytes());

        try {
            Authenticator.getUserAccess("BASIC " + unPasEnc, mockUrl);
            assertTrue(false);
        } catch (BadAuthException ex) {
            assertEquals(ex.getMessage(), "Incorrect username or password.");
        }
    }

    @Test
    public void testGetUserAccessInvalidPost() throws Exception{
        String mockUrl = "https://private-48ccba-amcrpass.apiary-mock.com/nonExistant";
        String unPasDec = "juraj.skvarla@spacesystems.cz:correctPass";
        String unPasEnc = Base64.encodeBase64String(unPasDec.getBytes());

        try {
            Authenticator.getUserAccess("BASIC "+unPasEnc, mockUrl);
            assertTrue(false);
        } catch (BadAuthException ex) {
            assertEquals(ex.getMessage(), "amcr-pass error.");
        }
    }
}
