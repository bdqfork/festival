package cn.bdqfork.ioc.exception;

/**
 * @author bdq
 * @date 2019-02-12
 */
public class SpringToyException extends Exception {
    public SpringToyException() {
    }

    public SpringToyException(String message) {
        super(message);
    }

    public SpringToyException(Throwable cause) {
        super(cause);
    }

    public SpringToyException(String message, Throwable cause) {
        super(message, cause);
    }
}
