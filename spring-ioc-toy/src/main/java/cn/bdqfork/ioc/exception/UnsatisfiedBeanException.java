package cn.bdqfork.ioc.exception;

/**
 * @author bdq
 * @date 2019-02-13
 */
public class UnsatisfiedBeanException extends SpringToyException {
    public UnsatisfiedBeanException(String message) {
        super(message);
    }
}
