package cn.bdqfork.web.route;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @since 2020/2/21
 */
public class ModelAndView {
    private String view;
    private Map<String, Object> model;

    public ModelAndView(String view) {
        this(view, new HashMap<>());
    }

    public ModelAndView(String view, Map<String, Object> model) {
        this.view = view;
        this.model = model;
    }

    public void add(String key, Object value) {
        model.put(key, value);
    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
