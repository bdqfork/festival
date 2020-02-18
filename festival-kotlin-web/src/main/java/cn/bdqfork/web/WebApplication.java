package cn.bdqfork.web;

/**
 * @author bdq
 * @since 2020/2/17
 */
public class WebApplication {
    public void run(Class<?> clazz) {
        String scanPath = clazz.getPackage().getName();
        try {
            new WebApplicationContext(scanPath).start();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
