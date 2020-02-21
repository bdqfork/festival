package cn.bdqfork.core.proxy.javassist;

import cn.bdqfork.core.util.ReflectUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019/12/23
 */
public abstract class Proxy implements Serializable {
    private static final Map<String, Object> CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    private static final AtomicLong PROXY_COUNTER = new AtomicLong(0);

    public Proxy() {
    }

    /**
     * 抽象方法，由代理子类实现，调用代理子类的构造方法生成代理实例
     *
     * @param handler InvocationHandler
     * @return 代理实例
     */
    public abstract Object newInstance(InvocationHandler handler);

    /**
     * 生成代理实例
     *
     * @param classLoader ClassLoader
     * @param interfaces  代理接口
     * @param handler     InvocationHandler
     * @return Object
     * @throws IllegalArgumentException 生成失败时抛出
     */
    public static Object newProxyInstance(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler handler) throws IllegalArgumentException {
        //通过接口名生成KEY来缓存实例
        String key = getKey(interfaces);
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        ClassGenerator generator = new ClassGenerator(classLoader);

        for (Class<?> interfaceClass : interfaces) {
            generator.addInterface(interfaceClass.getName());
        }

        //生成代理子类名称，例如Proxy0
        String className = Proxy.class.getName() + PROXY_COUNTER.getAndIncrement();

        generator.setClassName(className).setSuperClass(Proxy.class.getName());

        //与JDK类似，将接口方法作为代理子类的属性
        generator.addField("private java.lang.reflect.Method[] methods;");
        //将InvocationHandler也作为代理子类的属性
        generator.addField("private " + InvocationHandler.class.getName() + " handler;");

        //添加构造方法，初始化InvocationHandler
        generator.addConstructor(Modifier.PUBLIC, new Class[]{InvocationHandler.class}, "$0.handler=$1;");
        //添加默认构造方法
        generator.addDefaultConstructor();

        //扫描接口，获取所有的接口方法，并通过方法签名进行去重
        Set<String> worked = new HashSet<>();
        List<Method> methods = new ArrayList<>();
        for (Class<?> interfaceClass : interfaces) {
            for (Method method : interfaceClass.getMethods()) {
                if (worked.contains(ReflectUtils.getSignature(method))) {
                    continue;
                }
                worked.add(ReflectUtils.getSignature(method));
                methods.add(method);
            }
        }

        //根据接口方法信息，生成代理实例的方法实现
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            StringBuilder codeBuilder = new StringBuilder();
            codeBuilder.append("Object result = $0.handler.invoke($0,$0.methods[").append(i).append("],$args);");
            Class<?> returnType = method.getReturnType();
            if (!Void.TYPE.equals(returnType)) {
                codeBuilder.append("return ").append(castResult("result", returnType));
            }
            generator.addMethod(Modifier.PUBLIC, returnType, method.getName(),
                    method.getParameterTypes(), method.getExceptionTypes(), codeBuilder.toString());
        }

        //添加Proxy抽象方法的实现
        generator.addMethod("public Object newInstance(" + InvocationHandler.class.getName() + " handler){return new " + className + "($1);}");

        try {
            Class<?> clazz = generator.toClass();
            Proxy proxy = (Proxy) clazz.newInstance();
            //生成代理实例
            proxy = (Proxy) proxy.newInstance(handler);
            //为代理实例的methods属性赋值
            Field handlerField = clazz.getDeclaredField("methods");
            ReflectUtils.makeAccessible(handlerField);
            handlerField.set(proxy, methods.toArray(new Method[0]));

            CACHE.putIfAbsent(key, proxy);

            return proxy;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }

    }

    private static String getKey(Class<?>[] interfaces) {
        return Arrays.stream(interfaces)
                .map(Class::getName)
                .collect(Collectors.joining());
    }

    /**
     * 对方法返回值进行转换
     *
     * @param resultName 返回值名称
     * @param returnType 返回类型
     * @return
     */
    private static String castResult(String resultName, Class<?> returnType) {
        if (returnType.isPrimitive()) {
            if (Byte.TYPE == returnType) {
                return resultName + "==null? (byte)0:((Byte)" + resultName + ").byteValue();";
            }
            if (Short.TYPE == returnType) {
                return resultName + "==null? (short)0:((Short)" + resultName + ").shortValue();";
            }
            if (Integer.TYPE == returnType) {
                return resultName + "==null? (int)0:((Integer)" + resultName + ").intValue();";
            }
            if (Long.TYPE == returnType) {
                return resultName + "==null? (long)0:((Long)" + resultName + ").longValue();";
            }
            if (Float.TYPE == returnType) {
                return resultName + "==null? (float)0:((Float)" + resultName + ").floatValue();";
            }
            if (Double.TYPE == returnType) {
                return resultName + "==null? (double)0:((Double)" + resultName + ").doubleValue();";
            }
            if (Character.TYPE == returnType) {
                return resultName + "==null? (char)0:((Character)" + resultName + ").charValue();";
            }
            if (Boolean.TYPE == returnType) {
                return resultName + "==null? false:((Boolean)" + resultName + ").booleanValue();";
            }
            throw new RuntimeException("Unknow primitive " + returnType.getCanonicalName() + " !");
        }
        return "(" + returnType.getCanonicalName() + ")" + resultName + ";";
    }

}
