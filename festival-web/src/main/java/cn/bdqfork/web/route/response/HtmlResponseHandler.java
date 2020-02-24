package cn.bdqfork.web.route.response;

import cn.bdqfork.web.route.ModelAndView;
import cn.bdqfork.web.route.TemplateManager;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class HtmlResponseHandler extends AbstractResponseHandler {
    private TemplateManager templateManager;

    public HtmlResponseHandler(TemplateManager templateManager) {
        this.templateManager = templateManager;
    }

    @Override
    protected void doHandle(RoutingContext routingContext, Object result) throws Exception {
        HttpServerResponse response = routingContext.response();
        ModelAndView modelAndView = (ModelAndView) result;
        String template = templateManager.getTemplatePath() + "/" + modelAndView.getView() + templateManager.getSuffix();
        templateManager.getTemplateEngine().render(modelAndView.getModel(), template, res -> {
            if (res.succeeded()) {
                response.end(res.result());
            } else {
                routingContext.fail(500, res.cause());
            }
        });
    }

}
