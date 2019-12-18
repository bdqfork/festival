package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019/12/18
 */
public class NoSuchBeanException extends BeansException {
    public NoSuchBeanException(String message) {
        super(message);
    }
}
