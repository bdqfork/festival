package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-02-14
 */
public class FailedInjectedFieldException extends BeansException {
    public FailedInjectedFieldException(String message) {
        super(message);
    }

    public FailedInjectedFieldException(Throwable cause) {
        super(cause);
    }

    public FailedInjectedFieldException(String message, Throwable cause) {
        super(message, cause);
    }
}
