package cn.bdqfork.web.service;

import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.web.util.EventBusUtils;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/26
 */
public class ServiceVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(ServiceVerticle.class);
    private Object serviceBean;
    private Disposable disposable;

    public ServiceVerticle(Object serviceBean) {
        this.serviceBean = serviceBean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Completable rxStart() {
        EventBus eventBus = vertx.eventBus();
        Class<?> targetClass = AopUtils.getTargetClass(serviceBean);
        String address = EventBusUtils.getAddress(targetClass);
        disposable = eventBus.consumer(address)
                .toFlowable()
                .onBackpressureBuffer()
                .subscribe(msg -> {
                    MethodInvocation invocation = (MethodInvocation) msg.body();
                    Method method = serviceBean.getClass().getMethod(invocation.getMethodName(), invocation.getArgumentClasses());
                    try {
                        Flowable<Object> result = (Flowable<Object>) ReflectUtils.invokeMethod(serviceBean, method, invocation.getArguments());
                        result.subscribe(msg::reply, e -> msg.fail(500, e.getMessage()));
                    } catch (Exception e) {
                        msg.fail(500, e.getMessage());
                        if (log.isErrorEnabled()) {
                            log.error(e.getMessage(), e);
                        }
                    }
                });
        if (log.isInfoEnabled()) {
            log.info("deploy verticle service {}!", targetClass.getCanonicalName());
        }
        return super.rxStart();
    }

    @Override
    public Completable rxStop() {
        disposable.dispose();
        return super.rxStop();
    }

}
