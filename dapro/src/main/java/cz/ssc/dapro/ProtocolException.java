package cz.ssc.dapro;

public class ProtocolException extends Exception {
    private final Errors error;
    
    private final String arg;

    public ProtocolException(Errors error, String arg) {
        super(formatMessage(error.toString() + ": ", arg));
        
        this.error = error;
        this.arg = arg;
    }

    public Errors getError() {
        return error;
    }

    public String getArg() {
        return arg;
    }
    
    public static String formatMessage(String head, String tail) {
        String msg = head;
        if (tail != null) {
            msg += tail;
        }
        
        return msg;
    }
}
