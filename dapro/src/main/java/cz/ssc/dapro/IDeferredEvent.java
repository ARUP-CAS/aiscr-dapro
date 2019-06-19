package cz.ssc.dapro;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public interface IDeferredEvent {    
    void execute(ContentHandler ch) throws SAXException;
}
