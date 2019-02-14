package cn.bdqfork.ioc.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class ConstructorInjectedException extends InjectedException {
    private static final String MESSAGE = "failed to inject bean: %s by constructor!";

    public ConstructorInjectedException(String beanName, Throwable cause) {
        super(String.format(MESSAGE, beanName), cause);
    }
}
