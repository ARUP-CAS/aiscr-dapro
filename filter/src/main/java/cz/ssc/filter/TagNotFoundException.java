package cz.ssc.filter;

/**
 *
 * @author Juraj
 */
public class TagNotFoundException extends Exception {

    private String message;
    
    TagNotFoundException(String string) {
        message = string;
    }

    public String getMessage() {
        return message;
    }
    
}
