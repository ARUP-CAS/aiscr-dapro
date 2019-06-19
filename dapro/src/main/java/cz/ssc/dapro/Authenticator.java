package cz.ssc.dapro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 *
 * @author Juraj
 */
public class Authenticator {

    private static final Logger LOGGER = Logger.getLogger(Authenticator.class.getName());

    /**
     * @param authHeader
     * @param passUrl
     * @return
     * @throws IOException 
     * @throws cz.ssc.dapro.BadAuthException 
     */
    public static String getUserAccess(String authHeader, String passUrl) throws IOException, BadAuthException {

        if (authHeader == null) {
            LOGGER.log(Level.FINE, "No authentication header");
            return "A"; // Anonymous user HTTP 401 Unauthorized status
        }

        if (!authHeader.toUpperCase().startsWith("BASIC ")) {
            LOGGER.log(Level.INFO, "Incorrect authentication method, only accepts BASIC AUTH");
            throw new BadAuthException("Incorrect authentication method.");
        }
        
        // Decode the username and pass
        String nameAndPassEnc = authHeader.substring(6);
        String userpassDecoded = new String(Base64.decodeBase64(nameAndPassEnc));
        
        if(!userpassDecoded.contains(":")){
            LOGGER.log(Level.INFO, "Incorrect authentication method, BASIC AUTH missing colon");
            throw new BadAuthException("BASIC AUTH missing colon.");
        }
        
        String account[] = userpassDecoded.split(":", 2);
        
        // Do the request towards amcrpass
        return getUserLvl(account, passUrl);
    }

    /**
     * Post request towards PassServlet regarding user access level (A-E)
     * @param account
     * @return
     * @throws IOException 
     */
    private static String getUserLvl(String account[], String passUrl) throws IOException, BadAuthException {

        String userName = account[0];
        String pwd = account[1];

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("user", userName);
        jsonObj.put("pwd", pwd);
        
        URL obj = new URL(passUrl);
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");
        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(jsonObj.toString().getBytes());
        os.flush();
        os.close();
        int responseCode = postConnection.getResponseCode();
        LOGGER.log(Level.FINEST, "AMCRPASS POST Response Code: {0}", responseCode);
        LOGGER.log(Level.FINEST, "AMCRPASS POST Response Message: {0}", postConnection.getResponseMessage());

        if (responseCode == HttpURLConnection.HTTP_CREATED
                || responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    postConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject json = new JSONObject(response.toString());
            LOGGER.log(Level.FINEST, "Response json: {0}", json.toString());
            String accessLvl;
            
            if(json.has("error")){
                LOGGER.log(Level.FINE, "Unknown username or password for user {0}", userName);
                throw new BadAuthException("Incorrect username or password.");
            } else {
                // I assume that if its not error then there is "auth" keyword 
                accessLvl = json.getString("auth");
            }
            
            return accessLvl;
            
        } else {
            LOGGER.log(Level.WARNING, String.format("amcr-pass responded with %d.", responseCode));
            throw new BadAuthException("amcr-pass error.");
        }

    }

}
