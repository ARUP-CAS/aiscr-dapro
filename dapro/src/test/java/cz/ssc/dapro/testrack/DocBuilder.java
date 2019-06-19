package cz.ssc.dapro.testrack;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DocBuilder {
    private final DocumentBuilder documentBuilder;

    private final Schema schema;

    public DocBuilder() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        documentBuilder = dbf.newDocumentBuilder();

        String[] names = { "OAI-PMH.xsd", "oai_dc.xsd", "rightsManifest.xsd" };

        // our custom schemas are published as static pages
        String[] schemaNames = { "oai_rdf.xsd", "oai_amcr.xsd" };
        
        StreamSource[] schemaDocs = new StreamSource[names.length + schemaNames.length];
        for (int i = 0; i < names.length; ++i) {
            URL resource = getClass().getResource("/xslt/" + names[i]);
            schemaDocs[i] = new StreamSource(resource.openStream(), resource.toExternalForm());
        }

        // https://stackoverflow.com/questions/4948457/junit-maven-accessing-project-build-directory-value
        String localPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File localDir = new File(localPath);
        Path top = Paths.get(localDir.toString()).getParent().getParent();
        Path mediaPath = Paths.get(top.toString(), "src/main/webapp/media");
        for (int i = 0; i < schemaNames.length; ++i) {
            Path filePath = Paths.get(mediaPath.toString(), schemaNames[i]);
            schemaDocs[names.length + i] = new StreamSource(filePath.toFile());
        }

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        sf.setResourceResolver(new InternalResourceResolver());
        sf.setErrorHandler(new StrictErrorHandler());
        schema = sf.newSchema(schemaDocs);
    }

    public Document load(String xmlName) throws IOException, SAXException {
        String resourceName = "/data/" + xmlName;
        URL xu = getClass().getResource(resourceName);
        return documentBuilder.parse(xu.openStream());
    }

    public Document buildValid(byte[] content) throws IOException, SAXException {
        Document doc = documentBuilder.parse(new ByteArrayInputStream(content));
        Validator validator = schema.newValidator();
        validator.validate(new DOMSource(doc));
        return doc;
    }
}
