package cn.bdqfork.mvc.context;

import cn.bdqfork.core.util.AopUtils;
import cn.bdqfork.core.util.ReflectUtils;
import cn.bdqfork.mvc.util.EventBusUtils;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2020/1/26
 */
@Slf4j
public class ServiceVerticle extends AbstractVerticle {
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
                        result.doOnError(e -> {
                            if (log.isErrorEnabled()) {
                                log.error(e.getMessage(), e.getCause());
                            }
                        }).subscribe(msg::reply);
                    } catch (Exception e) {
                        msg.fail(500, e.getMessage());
                        if (log.isErrorEnabled()) {
                            log.error(e.getMessage(), e.getCause());
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
