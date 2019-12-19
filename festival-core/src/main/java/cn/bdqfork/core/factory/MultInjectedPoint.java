package cn.bdqfork.core.factory;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author bdq
 * @since 2019/12/18
 */
public class MultInjectedPoint extends InjectedPoint implements Iterable<InjectedPoint> {
    private List<InjectedPoint> injectedPoints = new LinkedList<>();

    public MultInjectedPoint() {
        this(true);
    }

    public MultInjectedPoint(boolean require) {
        super(Iterable.class, require);
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
}
