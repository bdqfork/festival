package cn.bdqfork.ioc.exception;

/**
 * @author bdq
 * @date 2019-02-14
 */
public class ResolvedException extends SpringToyException {
    public ResolvedException(String message) {
        super(message);
    }

    public ResolvedException(String message, Throwable cause) {
        super(message, cause);
    }
}
