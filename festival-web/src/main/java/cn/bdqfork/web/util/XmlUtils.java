package cn.bdqfork.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author bdq
 * @since 2020/2/20
 */
public class XmlUtils {
    private static XmlMapper xmlMapper = new XmlMapper();

    public static String toXml(Object instance) throws JsonProcessingException {
        return xmlMapper.writeValueAsString(instance);
    }

    public static <T> T fromXml(String xml, Class<T> clazz) throws JsonProcessingException {
        return xmlMapper.readValue(xml, clazz);
    }
}
