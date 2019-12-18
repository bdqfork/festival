package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-07-28
 */
public class FailedInjectedConstructorException extends BeansException {
    public FailedInjectedConstructorException(Throwable cause) {
        super(cause);
    }

    public FailedInjectedConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}
