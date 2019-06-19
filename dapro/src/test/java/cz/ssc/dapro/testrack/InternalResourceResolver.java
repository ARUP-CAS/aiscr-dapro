package cz.ssc.dapro.testrack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.xerces.dom.DOMInputImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class InternalResourceResolver implements LSResourceResolver {
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        int pos = systemId.lastIndexOf('/');
        String resourceName = "/xslt/" + systemId.substring(pos + 1);
        URL xu = getClass().getResource(resourceName);
        InputStream inputStream;
        try {
            inputStream = xu.openStream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        
        LSInput input = new DOMInputImpl();
        input.setPublicId(publicId);
        input.setSystemId(systemId);
        input.setBaseURI(baseURI);
        input.setCharacterStream(new InputStreamReader(inputStream));
        return input;
    }
}
