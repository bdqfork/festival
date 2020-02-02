package cn.bdqfork.web.convertor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2020/2/2
 */
public class DefaultConvertorFactory implements ConvertorFactory, ConvertorRegistry {
    private Map<Class<?>, Convertor<?, ?>> convertorMap = new ConcurrentHashMap<>(16);

    @Override
    public Convertor<?, ?> getConvertor(Class<?> targetType) {
        return convertorMap.get(targetType);
    }

    @Override
    public void registerConvertor(Class<?> targetType, Convertor<?, ?> convertor) {
        convertorMap.putIfAbsent(targetType, convertor);
    }
}
