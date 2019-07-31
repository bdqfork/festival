package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class ApplicationContextException extends Exception{
    public ApplicationContextException(String message) {
        super(message);
    }

    public ApplicationContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationContextException(Throwable cause) {
        super(cause);
    }
}
