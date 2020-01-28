package cn.bdqfork.mvc.context.handler;


import cn.bdqfork.mvc.context.MappingAttribute;

/**
 * @author bdq
 * @since 2020/1/21
 */
public interface RouterMappingHandler {

    void handle(MappingAttribute mappingAttribute);

}
