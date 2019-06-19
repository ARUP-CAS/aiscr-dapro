package cz.ssc.dapro;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class StartEvent implements IDeferredEvent {
    private final String uri;
    
    private final String localName;

    private final String qName;
    
    private final Attributes atts;

    public StartEvent(String uri,
            String localName, String qName,
            Attributes atts) {
        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
        this.atts = new AttributesImpl(atts);
    }

    @Override
    public void execute(ContentHandler ch) throws SAXException {
        ch.startElement(uri, localName, qName, atts);
    }
}
