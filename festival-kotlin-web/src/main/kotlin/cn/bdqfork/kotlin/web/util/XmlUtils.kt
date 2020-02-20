package cn.bdqfork.kotlin.web.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.dataformat.xml.XmlMapper

/**
 * @author bdq
 * @since 2020/2/20
 */
object XmlUtils {
    private val xmlMapper = XmlMapper()
    @Throws(JsonProcessingException::class)
    fun toXml(instance: Any): String {
        return xmlMapper.writeValueAsString(instance)
    }

    @Throws(JsonProcessingException::class)
    fun <T> fromXml(xml: String, clazz: Class<T>): T {
        return xmlMapper.readValue(xml, clazz)
    }
}