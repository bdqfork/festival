package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-07-30
 */
public class ScopeException extends ResolvedException {
    public ScopeException(String message) {
        super(message);
    }

    public ScopeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScopeException(Throwable cause) {
        super(cause);
    }
}
