package cn.bdqfork.context.aware;

import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.context.configuration.reader.ResourceReader;

/**
 * @author bdq
 * @since 2020/1/26
 */
public interface ResourceReaderAware {
    void setResourceReader(ResourceReader resourceReader) throws BeansException;
}
