package cn.bdqfork.core.factory.processor;

/**
 * 表示bean的优先级，用户实现的value必须大于0否则无效，数字越小优先级越高，未标记或未注明value的bean优先级为最低
 * @author bdq
 * @since 2020/1/29
 */
public interface OrderAware {
    int getOrder();
}
