package cn.bdqfork.kotlin.web.route.response

import cn.bdqfork.kotlin.web.route.ModelAndView
import cn.bdqfork.kotlin.web.route.TemplateManager
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.awaitResult
import kotlinx.coroutines.runBlocking

/**
 * @author bdq
 * @since 2020/2/21
 */
class HtmlResponseHandler(private val templateManager: TemplateManager) : AbstractResponseHandler() {
    @Throws(Exception::class)
    override fun doHandle(routingContext: RoutingContext, result: Any) = runBlocking {
        val response = routingContext.response()
        if (templateManager.isEnable) {
            val modelAndView = result as ModelAndView
            val template = templateManager.templatePath + "/" + modelAndView.view + templateManager.suffix
            val buffer = awaitResult<Buffer> { h -> templateManager.templateEngine.render(modelAndView.getModel(), template, h) }
            response.end(buffer)
        } else {
            routingContext.fail(500, IllegalStateException("template is not enabled!"))
        }
    }

}