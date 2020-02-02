package cn.bdqfork.web.convertor;

/**
 * @author bdq
 * @since 2020/2/2
 */
public interface ConvertorFactory {
    Convertor<?, ?> getConvertor(Class<?> targetType);
}
