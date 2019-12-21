package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019-02-14
 */
public class ConflictedBeanException extends BeansException {

    public ConflictedBeanException(String message) {
        super(message);
    }
}
