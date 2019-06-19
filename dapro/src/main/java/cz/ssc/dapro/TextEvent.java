package cz.ssc.dapro;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TextEvent implements IDeferredEvent {
    private final char arr[];
    
    public TextEvent(char chars[], int start,
            int length) {
        arr = new char[length];
        System.arraycopy(chars, start, arr, 0, length);
    }

    @Override
    public void execute(ContentHandler ch) throws SAXException {
        ch.characters(arr, 0, arr.length);
    }
}
