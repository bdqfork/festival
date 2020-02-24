package cn.bdqfork.kotlin.web.annotation

import javax.inject.Named
import javax.inject.Singleton

/**
 * 该注解用于将服务转化为Verticle，服务之间的调用将通过EventBus进行通信。
 *
 * @author bdq
 * @since 2020/1/21
 */
@Singleton
@Named
@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class VerticleMapping(val value: String = "")