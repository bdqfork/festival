package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class ConflictedBeanException extends SpringToyException {
    private static final String MESSAGE = "the bean named: %s has conflicted ! ";

    public ConflictedBeanException(String beanName) {
        super(String.format(MESSAGE, beanName));
    }
}
