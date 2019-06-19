package cz.ssc.dapro;

public class IdentUtil {
    public static String removeIdentifierHead(String external, Options options) throws ProtocolException {
        String prefix = options.getIdentifierHead();
        String ident = external.startsWith(prefix) ? external.substring(prefix.length()) : external;
        
        // valid format is complicated and not necessarily stable, but it 
        // should be around 15 characters
        if (ident.length() > 32) {
            throw new ProtocolException(Errors.badArgument, null);            
        }
        
        return ident;
    }
}
