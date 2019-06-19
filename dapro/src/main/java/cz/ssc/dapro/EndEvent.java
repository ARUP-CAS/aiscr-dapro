package cz.ssc.dapro;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EndEvent implements IDeferredEvent {
    private final String uri;
    
    private final String localName;

    private final String qName;
    
    public EndEvent(String uri, String localName, String qName) {
        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
    }

    @Override
    public void execute(ContentHandler ch) throws SAXException {
        ch.endElement(uri, localName, qName);
    }
}
