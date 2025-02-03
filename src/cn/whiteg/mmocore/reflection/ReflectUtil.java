package cn.whiteg.mmocore.reflection;


import java.lang.reflect.*;
import java.util.Arrays;

public class ReflectUtil {
//    笨办法x 需要添加启动参数
//    需要添加启动参数"--add-exports java.base/jdk.internal.reflect=ALL-UNNAMED"
//    private static final ReflectionFactory reflection = AccessController.doPrivileged(
//            new ReflectionFactory.GetReflectionFactoryAction());
//    private static MethodAccessor newFieldAccessorMethod;
//    static {
//        try{
//            var clazz = ClassLoader.getSystemClassLoader().loadClass("jdk.internal.reflect.UnsafeFieldAccessorFactory");
//            var method = clazz.getDeclaredMethod("newFieldAccessor",Field.class,boolean.class);
//            newFieldAccessorMethod = reflection.newMethodAccessor(method);
//        }catch (ClassNotFoundException | NoSuchMethodException e){
//            e.printStackTrace();
//        }
//    }

    /**
     * * 检查所有的修饰符，是否是 public static final
     * * @param modify
     */
    public static void checkModifier(int modify) {
        System.out.println("当前的 modify : " + modify);
        System.out.println(" public : " + Modifier.isPublic(modify));
        System.out.println(" static : " + Modifier.isStatic(modify));
        System.out.println(" final : " + Modifier.isFinal(modify));
    }


    //反射获取Field
    public static Field getFieldAndAccessible(Class<?> c,String name) throws NoSuchFieldException {
        Field f = c.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    //构建私有对象
    public static <T> T newInstance(Class<? extends T> clazz,Object... values) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Class<?>[] types = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            types[i] = values[i].getClass();
        }
        Constructor<? extends T> con = clazz.getDeclaredConstructor(types);
        con.setAccessible(true);
        return con.newInstance(values);
    }

    /**
     * 检查类组是否有继承
     *
     * @param shr   源类组
     * @param type2 被检查的组
     * @return 是否继承
     */
    public static boolean hasTypes(Class<?>[] shr,Class<?>[] type2) {
        if (shr.length == type2.length){
            for (int i = 0; i < shr.length; i++) if (!shr[i].isAssignableFrom(type2[i])) return false;
            return true;
        }
        return false;
    }

    //根据类型获取Field
    public static Field getFieldFormType(Class<?> clazz,Class<?> type) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getType().equals(type)) return declaredField;
        }

        //如果有父类 检查父类
        var superClass = clazz.getSuperclass();
        if (superClass != null) return getFieldFormType(superClass,type);
        throw new NoSuchFieldException(type.getName());
    }

    //根据类型获取Field(针对泛型)
    public static Field getFieldFormType(Class<?> clazz,String type) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getAnnotatedType().getType().getTypeName().equals(type)) return declaredField;
        }
        //如果有父类 检查父类
        var superClass = clazz.getSuperclass();
        if (superClass != null) return getFieldFormType(superClass,type);
        throw new NoSuchFieldException(type);
    }

    //从数组结构中查找Field
    public static Field[] getFieldFormStructure(Class<?> clazz,Class<?>... types) throws NoSuchFieldException {
        var fields = clazz.getDeclaredFields();
        Field[] result = new Field[types.length];
        int index = 0;
        for (Field f : fields) {
            if (f.getType() == types[index]){
                result[index] = f;
                index++;
                if (index >= types.length){
                    return result;
                }
            } else {
                index = 0;
            }
        }
        throw new NoSuchFieldException(Arrays.toString(types));
    }
    //从数组结构中查找Field
    public static Field[] getFieldFormStructure(Class<?> clazz,String... types) throws NoSuchFieldException {
        var fields = clazz.getDeclaredFields();
        Field[] result = new Field[types.length];
        int index = 0;
        for (Field f : fields) {
            if (f.getGenericType().getTypeName().equals(types[index])){
                result[index] = f;
                index++;
                if (index >= types.length){
                    return result;
                }
            } else {
                index = 0;
            }
        }
        throw new NoSuchFieldException(Arrays.toString(types));
    }


    public static String makeGenericTypes(Class<?> clazz,String... types) {
        if (types.length <= 0) return clazz.getName();
        StringBuilder builder = new StringBuilder(clazz.getName()).append('<');
        if (types.length > 1){
            builder.append(String.join(", ",types));
        } else {
            builder.append(types[0]);
        }
        builder.append('>');
        return builder.toString();
    }


    public static String makeGenericTypes(Class<?> clazz,Class<?>... types) {
        if (types.length <= 0) return clazz.getName();
        StringBuilder builder = new StringBuilder(clazz.getName()).append('<');
        if (types.length > 1){
            String[] names = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                names[i] = types[i].getName();
            }
            builder.append(String.join(", ",names));
        } else {
            builder.append(types[0].getName());
        }
        builder.append('>');
        return builder.toString();
    }

    public static String makeGenericTypes(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * Search for the first publicly and privately defined method of the given name and parameter count.
     *
     * @param clazz      - a class to start with.
     * @param methodName - the method name, or NULL to skip.
     * @param returnType - the expected return type, or NULL to ignore.
     * @param params     - the expected parameters.
     * @return An object that invokes this specific method.
     * @throws IllegalStateException If we cannot find this method.
     */
    public static <RT> MethodInvoker<RT> getTypedMethod(Class<?> clazz,String methodName,Class<RT> returnType,Class<?>... params) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if ((methodName == null || method.getName().equals(methodName))
                    && (returnType == null || method.getReturnType().equals(returnType))
                    && (Arrays.equals(method.getParameterTypes(),params))){
                method.setAccessible(true);

                return new MethodInvoker<>(method);
            }
        }

        // Search in every superclass
        if (clazz.getSuperclass() != null)
            return (MethodInvoker<RT>) getTypedMethod(clazz.getSuperclass(),methodName,returnType,params);
        throw new IllegalStateException(String.format("Unable to find method %s %s (%s).",returnType,methodName,Arrays.asList(params)));
    }
}
