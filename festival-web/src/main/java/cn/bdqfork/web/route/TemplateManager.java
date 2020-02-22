package cn.bdqfork.web.route;

import io.vertx.ext.web.common.template.TemplateEngine;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class TemplateManager {
    public static final String DEFAULT_TEMPLATE_PATH = "template";
    /**
     * 模板引擎
     */
    private TemplateEngine templateEngine;
    /**
     * 模板路径
     */
    private String templatePath = DEFAULT_TEMPLATE_PATH;
    /**
     * 模板类型
     */
    private String templateType;
    /**
     * 模板文件后缀
     */
    private String suffix;
    /**
     * 是否开启
     */
    private boolean enable;

    public TemplateManager(boolean enable) {
        this.enable = enable;
    }

    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    public void setTemplateEngine(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isEnable() {
        return enable;
    }
}
