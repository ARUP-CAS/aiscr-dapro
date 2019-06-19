package cz.ssc.dapro;

/**
 *
 * @author Juraj
 */
public class BadAuthException extends Exception {
    public BadAuthException(String msg) {
        super(msg);
    }
}
