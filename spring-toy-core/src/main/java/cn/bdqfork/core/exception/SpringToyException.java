package cn.bdqfork.core.exception;

/**
 * 自定义异常
 *
 * @author bdq
 * @date 2019-02-12
 */
public class SpringToyException extends RuntimeException {
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
