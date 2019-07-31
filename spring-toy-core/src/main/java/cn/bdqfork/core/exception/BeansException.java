package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class BeansException extends ApplicationContextException {
    public BeansException(String message) {
        super(message);
    }

    public BeansException(Throwable cause) {
        super(cause);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
}
