package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class FieldInjectedException extends InjectedException {
    private static final String MESSAGE = "failed to inject bean: %s by field!";

    public FieldInjectedException(String beanName, Throwable cause) {
        super(String.format(MESSAGE, beanName), cause);
    }
}
