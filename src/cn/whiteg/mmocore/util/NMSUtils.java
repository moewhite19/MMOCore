package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.reflection.MethodInvoker;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class NMSUtils {
    private static Field craftEntity;

    private static String craftRoot;

    private static Field craftWorld;

    private static Field craftServer;

    static {

        try{
            craftRoot = Bukkit.getServer().getClass().getPackage().getName();
            //从Bukkit实体获取Nms实体
            var clazz = EntityUtils.class.getClassLoader().loadClass(craftRoot + ".entity.CraftEntity");
            craftEntity = NMSUtils.getFieldFormType(clazz,Entity.class);
            craftEntity.setAccessible(true);
            //获取world的Nms
            clazz = EntityUtils.class.getClassLoader().loadClass(craftRoot + ".CraftWorld");
            craftWorld = NMSUtils.getFieldFormType(clazz,WorldServer.class);
            craftWorld.setAccessible(true);
            clazz = EntityUtils.class.getClassLoader().loadClass(craftRoot + ".CraftServer");
            craftServer = NMSUtils.getFieldFormType(clazz,DedicatedServer.class);
            craftServer.setAccessible(true);
        }catch (Exception e){
            e.printStackTrace();
        }

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

    //根据实体Class获取实体Types
    public static <T extends Entity> EntityTypes<T> getEntityType(Class<? extends Entity> clazz) {
        String name = EntityTypes.class.getName().concat("<").concat(clazz.getName()).concat(">");
        for (Field field : EntityTypes.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue; //跳过非静态Field
            try{
                if (field.getAnnotatedType().getType().getTypeName().equals(name))
                    //noinspection unchecked
                    return (EntityTypes<T>) field.get(null);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    //根据实体Class获取Tile方块实体Types
    public static <T extends TileEntity> TileEntityTypes<T> getTileEntityType(Class<? extends TileEntity> clazz) {
        String name = TileEntityTypes.class.getName().concat("<").concat(clazz.getName()).concat(">");
        for (Field field : TileEntityTypes.class.getFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue; //跳过非静态Field
            try{
                if (field.getAnnotatedType().getType().getTypeName().equals(name))
                    //noinspection unchecked
                    return (TileEntityTypes<T>) field.get(null);
            }catch (IllegalAccessException e){
                e.printStackTrace();
            }
        }
        return null;
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

    public static Entity getNmsEntity(org.bukkit.entity.Entity entity) {
        try{
            return (Entity) craftEntity.get(entity);
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    public static WorldServer getNmsWorld(World world) {
        try{
            return (WorldServer) craftWorld.get(world);
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    public static DedicatedServer getNmsServer() {
        try{
            return (DedicatedServer) craftServer.get(Bukkit.getServer());
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }
}
