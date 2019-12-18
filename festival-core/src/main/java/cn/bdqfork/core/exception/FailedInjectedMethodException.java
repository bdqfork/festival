package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-02-14
 */
public class FailedInjectedMethodException extends BeansException {

    public FailedInjectedMethodException(String message) {
        super(message);
    }

    public FailedInjectedMethodException(Throwable cause) {
        super(cause);
    }

    public FailedInjectedMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
