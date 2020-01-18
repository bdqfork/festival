package cn.bdqfork.core.factory;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

/**
 * 多重注入点（注入点迭代器）
 * @author bdq
 * @since 2019/12/18
 */
public class MultInjectedPoint extends InjectedPoint implements Iterable<InjectedPoint> {

    private List<InjectedPoint> injectedPoints = new LinkedList<>();

    public MultInjectedPoint() {
        this(true);
    }

    public MultInjectedPoint(boolean require) {
        super(MultInjectedPoint.class, require);
    }

    public void addInjectedPoint(InjectedPoint injectedPoint) {
        injectedPoints.add(injectedPoint);
    }

    public void addInjectedPoints(List<InjectedPoint> injectedPoints) {
        this.injectedPoints.addAll(injectedPoints);
    }

    public Type[] getTypes() {
        return injectedPoints.stream()
                .map(InjectedPoint::getType)
                .toArray(Type[]::new);
    }

    public Class<?>[] getClassTypes() {
        return injectedPoints.stream()
                .map(InjectedPoint::getClassType)
                .toArray(Class<?>[]::new);
    }

    public Class<?>[] getActualTypes() {
        return injectedPoints.stream()
                .map(InjectedPoint::getActualType)
                .toArray(Class<?>[]::new);
    }

    public void clear() {
        injectedPoints.clear();
    }

    @Override
    public Iterator<InjectedPoint> iterator() {
        return injectedPoints.iterator();
    }

    @Override
    public void forEach(Consumer<? super InjectedPoint> action) {
        injectedPoints.forEach(action);
    }

    @Override
    public Spliterator<InjectedPoint> spliterator() {
        return injectedPoints.spliterator();
    }

    @Override
    public Object getValue() {
        return injectedPoints.stream().map(InjectedPoint::getValue).toArray(Object[]::new);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) {
        if (value instanceof Map) {
            Map<Integer, Object> valueMap = (Map<Integer, Object>) value;
            valueMap.forEach((k, v) -> injectedPoints.get(k).setValue(v));
        }
    }
}
