package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class FieldInjectedException extends BeansException {
    public FieldInjectedException(String message) {
        super(message);
    }

    public FieldInjectedException(Throwable cause) {
        super(cause);
    }

    public FieldInjectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
