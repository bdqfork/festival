package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-02-13
 */
public class UnsatisfiedBeanException extends BeansException {
    public UnsatisfiedBeanException(String message) {
        super(message);
    }

    public UnsatisfiedBeanException(Throwable cause) {
        super(cause);
    }
}
