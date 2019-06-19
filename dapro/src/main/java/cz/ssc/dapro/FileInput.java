package cz.ssc.dapro;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class FileInput {
    private final AtomicInteger counter;

    public FileInput() {
        counter = new AtomicInteger(0);
    }

    public Source filter(String path, FilterSpec spec) 
            throws TransformerConfigurationException, IOException, SAXException {
        if (path == null) {
            throw new IllegalArgumentException();
        }

        if (spec == null) {
            throw new IllegalArgumentException();
        }

        SAXTransformerFactory factory = (SAXTransformerFactory)(TransformerFactory.newInstance());
        TransformerHandler serializer = factory.newTransformerHandler();
        DOMResult result = new DOMResult();
        serializer.setResult(result);

        BulkFilter filter = new BulkFilter(spec);
        filter.setContentHandler(serializer);

        XMLReader xmlreader = XMLReaderFactory.createXMLReader();
        xmlreader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlreader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        xmlreader.setContentHandler(filter);
        
        try (FileInputStream input = new FileInputStream(path)) {
            InputSource in = new InputSource(input);
            in.setSystemId(nextSystemId());
            xmlreader.parse(in);
        }

        DOMSource out = new DOMSource(result.getNode());
        out.setSystemId(nextSystemId());
        return out;
    }

    public String nextSystemId() {
        int id = counter.getAndIncrement();
        return Integer.toString(id);
    }
}
