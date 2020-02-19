package cn.bdqfork.core.proxy.javassist;

import javassist.*;

import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/12/23
 */
public class ClassGenerator {
    /**
     * ClassPool缓存
     */
    private static final Map<ClassLoader, ClassPool> POOL_CACHE = new ConcurrentHashMap<>();
    /**
     * 构造方法名占位符
     */
    private static final String INIT_FLAG = "<init>";
    /**
     * Javassist ClassPool
     */
    private ClassPool classPool;
    /**
     * 全类名
     */
    private String className;
    /**
     * 简单类名
     */
    private String simpleName;
    /**
     * 父类
     */
    private String superClass;
    /**
     * 是否添加默认构造方法
     */
    private boolean addDefaultConstructor;
    /**
     * 需要实现的接口
     */
    private List<String> interfaces;
    /**
     * 构造方法
     */
    private List<String> constructors;
    /**
     * 属性
     */
    private List<String> fields;
    /**
     * 方法，包括实现接口中的所有方法
     */
    private List<String> methods;

    public ClassGenerator() {
        this(null);
    }

    public ClassGenerator(ClassLoader classLoader) {
        if (classLoader == null) {
            classPool = ClassPool.getDefault();
        } else {
            classPool = POOL_CACHE.get(classLoader);
            if (classPool == null) {
                classPool = new ClassPool(true);
                classPool.appendClassPath(new LoaderClassPath(classLoader));
                POOL_CACHE.putIfAbsent(classLoader, classPool);
            }
        }
    }

    private static String modifier(int modfier) {
        StringBuilder modifier = new StringBuilder();
        if (Modifier.isPublic(modfier)) {
            modifier.append("public");
        }
        if (Modifier.isProtected(modfier)) {
            modifier.append("protected");
        }
        if (Modifier.isPrivate(modfier)) {
            modifier.append("private");
        }

        if (Modifier.isStatic(modfier)) {
            modifier.append(" static");
        }
        if (Modifier.isVolatile(modfier)) {
            modifier.append(" volatile");
        }

        return modifier.toString();
    }

    public ClassGenerator setClassName(String className) {
        this.className = className;
        this.simpleName = className.substring(className.lastIndexOf(".") + 1);
        return this;
    }

    public ClassGenerator setSuperClass(String superClass) {
        this.superClass = superClass;
        return this;
    }

    public ClassGenerator addInterface(String interfaceName) {
        if (interfaces == null) {
            interfaces = new LinkedList<>();
        }
        interfaces.add(interfaceName);
        return this;
    }

    public ClassGenerator addConstructor(String constructor) {
        if (constructors == null) {
            constructors = new LinkedList<>();
        }
        constructors.add(constructor);
        return this;
    }

    public ClassGenerator addConstructor(int modifier, Class<?>[] parameters, String body) {
        return addConstructor(modifier, parameters, null, body);
    }

    /**
     * 生成构造方法，最终生成的方法文本示例如下
     * modifier &lt;init&gt; (parameters) throws exceptions{
     * body
     * }
     *
     * @param modifier       修饰符
     * @param parameterTypes 参数
     * @param exceptionTypes 异常
     * @param body           方法体
     * @return ClassGenerator
     */
    public ClassGenerator addConstructor(int modifier, Class<?>[] parameterTypes, Class<?>[] exceptionTypes, String body) {
        StringBuilder codeBuilder = new StringBuilder();
        //添加方法修饰符
        codeBuilder.append(modifier(modifier))
                .append(" ")
                .append(INIT_FLAG)
                .append("(");
        //添加参数
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                codeBuilder.append(",");
            }
            Class<?> parameter = parameterTypes[i];
            codeBuilder.append(parameter.getCanonicalName())
                    .append(" ")
                    .append("arg")
                    .append(i);
        }
        codeBuilder.append(")");
        //判断是否有异常，如果有，添加异常抛出
        if (exceptionTypes != null && exceptionTypes.length > 0) {
            codeBuilder.append("throws ");
            for (int i = 0; i < exceptionTypes.length; i++) {
                if (i > 0) {
                    codeBuilder.append(",");
                }
                Class<?> exceptionClass = exceptionTypes[i];
                codeBuilder.append(exceptionClass.getCanonicalName());
            }
        }
        //添加方法体
        codeBuilder.append("{")
                .append(body)
                .append("}");
        return addConstructor(codeBuilder.toString());
    }

    public ClassGenerator addDefaultConstructor() {
        addDefaultConstructor = true;
        return this;
    }

    public ClassGenerator addField(String field) {
        if (fields == null) {
            fields = new LinkedList<>();
        }
        fields.add(field);
        return this;
    }

    public ClassGenerator addMethod(String method) {
        if (methods == null) {
            methods = new LinkedList<>();
        }
        methods.add(method);
        return this;
    }

    /**
     * 生成方法代码文本，生成的示例如下
     * modifier returnType methodName(parameters) throws exceptions{
     * body
     * }
     *
     * @param modifier       修饰符
     * @param returnType     返回类型
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param exceptionTypes 异常类型
     * @param body           方法体
     * @return ClassGenerator
     */
    public ClassGenerator addMethod(int modifier, Class<?> returnType, String methodName, Class<?>[] parameterTypes,
                                    Class<?>[] exceptionTypes, String body) {
        StringBuilder methodBuilder = new StringBuilder();
        methodBuilder.append(modifier(modifier))
                .append(" ")
                .append(returnType.getName());
        methodBuilder.append(" ").append(methodName).append("(");

        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                methodBuilder.append(",");
            }
            methodBuilder.append(parameterTypes[i].getName())
                    .append(" arg")
                    .append(i);
        }
        methodBuilder.append(")");

        if (exceptionTypes != null && exceptionTypes.length > 0) {
            methodBuilder.append("throws ");

            for (int i = 0; i < exceptionTypes.length; i++) {
                if (i > 0) {
                    methodBuilder.append(",");
                }
                methodBuilder.append(exceptionTypes[i].getName());
            }
        }

        methodBuilder.append("{").append(body).append("}");

        addMethod(methodBuilder.toString());
        return this;
    }

    public Class<?> toClass() {
        return toClass(ClassGenerator.class.getClassLoader(), ClassGenerator.class.getProtectionDomain());
    }

    /**
     * 构建CtClass并转换为Class返回给调用者
     *
     * @param classLoader      ClassLoader
     * @param protectionDomain ProtectionDomain
     * @return 生成的Class
     */
    public Class<?> toClass(ClassLoader classLoader, ProtectionDomain protectionDomain) {
        try {
            CtClass ctClass = classPool.makeClass(className);

            if (superClass != null) {
                ctClass.setSuperclass(classPool.get(superClass));
            }

            if (interfaces != null) {
                for (String interfaceName : interfaces) {
                    ctClass.addInterface(classPool.get(interfaceName));
                }
            }

            if (addDefaultConstructor) {
                ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
            }

            if (fields != null) {
                for (String field : fields) {
                    ctClass.addField(CtField.make(field, ctClass));
                }
            }

            if (constructors != null) {
                for (String constructor : constructors) {
                    if (constructor.contains(INIT_FLAG)) {
                        constructor = constructor.replace(INIT_FLAG, simpleName);
                    }
                    ctClass.addConstructor(CtNewConstructor.make(constructor, ctClass));
                }
            }

            if (methods != null) {
                for (String method : methods) {
                    ctClass.addMethod(CtNewMethod.make(method, ctClass));
                }
            }

            return ctClass.toClass(classLoader, protectionDomain);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
