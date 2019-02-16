package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class ConstructorInjectedException extends InjectedException {

    public ConstructorInjectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
