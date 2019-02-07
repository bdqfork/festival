package cn.bdqfork.ioc;

import cn.bdqfork.ioc.utils.ReflectUtil;

import java.util.List;

/**
 * @author bdq
 * @date 2019-02-03
 */
public class Main {

    public static void main(String[] args) {
        List<Class<?>> classes = ReflectUtil.getClasses("cn");
        classes.forEach(clazz -> System.out.println(clazz.getName()));
    }

}
