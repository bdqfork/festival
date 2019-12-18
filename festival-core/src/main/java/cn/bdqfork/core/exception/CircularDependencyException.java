package cn.bdqfork.core.exception;

/**
 * @author bdq
 * @since 2019/12/18
 */
public class CircularDependencyException extends BeansException {
    public CircularDependencyException(String message) {
        super(message);
    }
}
