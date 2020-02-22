package cn.bdqfork.kotlin.web.route

import cn.bdqfork.context.configuration.reader.ResourceReader
import cn.bdqfork.core.exception.BeansException
import cn.bdqfork.core.factory.BeanFactory
import cn.bdqfork.core.util.StringUtils
import cn.bdqfork.kotlin.web.constant.ContentType
import cn.bdqfork.kotlin.web.constant.ServerProperty
import cn.bdqfork.kotlin.web.route.response.HtmlResponseHandler
import cn.bdqfork.kotlin.web.route.response.ResponseHandlerFactory
import io.vertx.core.Vertx
import io.vertx.ext.web.common.template.TemplateEngine
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine
import io.vertx.ext.web.templ.jade.JadeTemplateEngine
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine
import org.slf4j.LoggerFactory

/**
 * @author bdq
 * @since 2020/2/21
 */
class TemplateManager(private val beanFactory: BeanFactory) {
    /**
     * 模板引擎
     */
    lateinit var templateEngine: TemplateEngine
    /**
     * 模板路径
     */
    lateinit var templatePath: String
    /**
     * 模板类型
     */
    lateinit var templateType: String
    /**
     * 模板文件后缀
     */
    lateinit var suffix: String
    /**
     * 是否开启
     */
    var isEnable = false
        private set

    init {
        val resourceReader = try {
            beanFactory.getBean(ResourceReader::class.java)
        } catch (e: BeansException) {
            throw IllegalStateException(e)
        }

        isEnable = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_ENABLE, Boolean::class.java, false)
        if (isEnable) {

            if (log.isInfoEnabled) {
                log.info("template enabled!")
            }

            templateType = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_TYPE, String::class.java)
            check(!StringUtils.isEmpty(templateType)) { "template type not set!" }

            templateEngine = createTemplateEngine(templateType)

            val cacheEnable = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_CACHE_ENABLE, Boolean::class.java, true)
            System.setProperty("io.vertx.ext.web.common.template.TemplateEngine.disableCache", (!cacheEnable).toString())

            suffix = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_SUFFIX, String::class.java)
            check(!StringUtils.isEmpty(suffix)) { "template suffix not set!" }

            templatePath = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_PATH, String::class.java, DEFAULT_TEMPLATE_PATH)

            val responseHandlerFactory = try {
                beanFactory.getBean(ResponseHandlerFactory::class.java)
            } catch (e: BeansException) {
                throw IllegalStateException(e)
            }

            responseHandlerFactory.registerResponseHandler(ContentType.HTML, HtmlResponseHandler(this))
        }
    }

    private fun createTemplateEngine(templateType: String?): TemplateEngine {
        val vertx = try {
            beanFactory.getBean(Vertx::class.java)
        } catch (e: BeansException) {
            throw IllegalStateException(e)
        }
        return when (templateType) {
            "freemarker" -> FreeMarkerTemplateEngine.create(vertx)
            "thymeleaf" -> ThymeleafTemplateEngine.create(vertx)
            "jade" -> JadeTemplateEngine.create(vertx)
            else -> throw IllegalStateException(String.format("unsupported type of template %s!", templateType))
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TemplateManager::class.java)
        const val DEFAULT_TEMPLATE_PATH = "template"
    }

}