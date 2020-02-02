package cn.bdqfork.web.convertor;

/**
 * @author bdq
 * @since 2020/2/2
 */
public interface Convertor<S, T> {
    T convert(S value);
}
