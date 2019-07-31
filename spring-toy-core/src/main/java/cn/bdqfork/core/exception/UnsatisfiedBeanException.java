package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class UnsatisfiedBeanException extends BeansException {
    public UnsatisfiedBeanException(String message) {
        super(message);
    }
}
