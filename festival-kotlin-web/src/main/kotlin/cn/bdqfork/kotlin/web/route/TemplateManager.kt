package cn.bdqfork.kotlin.web.route

import io.vertx.ext.web.common.template.TemplateEngine

/**
 * @author bdq
 * @since 2020/2/21
 */
class TemplateManager(val isEnable: Boolean) {
    /**
     * 模板引擎
     */
    lateinit var templateEngine: TemplateEngine
    /**
     * 模板类型
     */
    lateinit var templateType: String
    /**
     * 模板文件后缀
     */
    lateinit var suffix: String
    /**
     * 模板路径
     */
    var templatePath = DEFAULT_TEMPLATE_PATH

    companion object {
        const val DEFAULT_TEMPLATE_PATH = "template"
    }

}