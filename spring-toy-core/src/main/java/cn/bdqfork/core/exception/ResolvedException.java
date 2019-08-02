package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class ResolvedException extends ApplicationContextException {
    public ResolvedException(String message) {
        super(message);
    }

    public ResolvedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResolvedException(Throwable cause) {
        super(cause);
    }
}
