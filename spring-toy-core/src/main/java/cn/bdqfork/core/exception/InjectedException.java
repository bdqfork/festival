package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class InjectedException extends Exception {

    public InjectedException(String message) {
        super(message);
    }

    public InjectedException(Throwable cause) {
        super(cause);
    }

    public InjectedException(String message, Throwable cause) {
        super(message, cause);
    }

}
