package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class ConflictedBeanException extends BeansException {

    public ConflictedBeanException(String message) {
        super(message);
    }
}
