package cn.bdqfork.web.route;

import cn.bdqfork.context.configuration.reader.ResourceReader;
import cn.bdqfork.core.exception.BeansException;
import cn.bdqfork.core.factory.BeanFactory;
import cn.bdqfork.core.util.StringUtils;
import cn.bdqfork.web.constant.ContentType;
import cn.bdqfork.web.constant.ServerProperty;
import cn.bdqfork.web.route.response.HtmlResponseHandler;
import cn.bdqfork.web.route.response.ResponseHandlerFactory;
import io.vertx.core.Vertx;
import io.vertx.ext.web.common.template.TemplateEngine;
import io.vertx.ext.web.templ.freemarker.FreeMarkerTemplateEngine;
import io.vertx.ext.web.templ.jade.JadeTemplateEngine;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class TemplateManager {
    private static final Logger log = LoggerFactory.getLogger(TemplateManager.class);
    public static final String DEFAULT_TEMPLATE_PATH = "template";

    private BeanFactory beanFactory;
    /**
     * 模板引擎
     */
    private TemplateEngine templateEngine;
    /**
     * 模板路径
     */
    private String templatePath;
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

    public TemplateManager(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void init() {
        ResourceReader resourceReader;
        try {
            resourceReader = beanFactory.getBean(ResourceReader.class);
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }

        this.enable = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_ENABLE, Boolean.class, false);

        if (this.enable) {
            if (log.isInfoEnabled()) {
                log.info("template enabled!");
            }

            this.templateType = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_TYPE, String.class);
            if (StringUtils.isEmpty(templateType)) {
                throw new IllegalStateException("template type not set!");
            }

            this.templateEngine = createTemplateEngine(templateType);

            Boolean cacheEnable = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_CACHE_ENABLE, Boolean.class, true);

            System.setProperty("io.vertx.ext.web.common.template.TemplateEngine.disableCache", String.valueOf((!cacheEnable)));

            this.suffix = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_SUFFIX, String.class);
            if (StringUtils.isEmpty(suffix)) {
                throw new IllegalStateException("template suffix not set!");
            }

            this.templatePath = resourceReader.readProperty(ServerProperty.SERVER_TEMPLATE_PATH, String.class, DEFAULT_TEMPLATE_PATH);

            ResponseHandlerFactory responseHandlerFactory;
            try {
                responseHandlerFactory = beanFactory.getBean(ResponseHandlerFactory.class);
            } catch (BeansException e) {
                throw new IllegalStateException(e);
            }

            responseHandlerFactory.registerResponseHandler(ContentType.HTML, new HtmlResponseHandler(this));
        }
    }


    private TemplateEngine createTemplateEngine(String templateType) {
        Vertx vertx;
        try {
            vertx = beanFactory.getBean(Vertx.class);
        } catch (BeansException e) {
            throw new IllegalStateException(e);
        }

        TemplateEngine templateEngine;//todo:把switch换成if
        switch (templateType) {

            case "freemarker":
                templateEngine = FreeMarkerTemplateEngine.create(vertx);
                break;

            case "thymeleaf":
                templateEngine = ThymeleafTemplateEngine.create(vertx);
                break;

            case "jade":
                templateEngine = JadeTemplateEngine.create(vertx);
                break;

            default:
                throw new IllegalStateException(String.format("unsupported type of template %s!", templateType));
        }
        return templateEngine;
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
