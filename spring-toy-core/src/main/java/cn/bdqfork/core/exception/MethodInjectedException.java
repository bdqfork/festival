package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class MethodInjectedException extends InjectedException {
    private static final String MESSAGE = "failed to inject bean: %s by method!";

    public MethodInjectedException(String beanName, Throwable cause) {
        super(String.format(MESSAGE, beanName), cause);
    }
}
