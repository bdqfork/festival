package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class MethodInjectedException extends BeansException {

    public MethodInjectedException(String message, Throwable cause) {
        super(message, cause);
    }
}
