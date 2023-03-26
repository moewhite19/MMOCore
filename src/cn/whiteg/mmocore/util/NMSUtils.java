package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.reflection.MethodInvoker;
import cn.whiteg.mmocore.reflection.ReflectUtil;
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
            craftEntity = ReflectUtil.getFieldFormType(clazz,Entity.class);
            craftEntity.setAccessible(true);
            //获取world的Nms
            clazz = EntityUtils.class.getClassLoader().loadClass(craftRoot + ".CraftWorld");
            craftWorld = ReflectUtil.getFieldFormType(clazz,WorldServer.class);
            craftWorld.setAccessible(true);
            clazz = EntityUtils.class.getClassLoader().loadClass(craftRoot + ".CraftServer");
            craftServer = ReflectUtil.getFieldFormType(clazz,DedicatedServer.class);
            craftServer.setAccessible(true);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //根据类型获取Field
    @Deprecated(since = "不应该在这里的方法，已移动到ReflectUtil内")
    public static Field getFieldFormType(Class<?> clazz,Class<?> type) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getType().equals(type)) return declaredField;
        }

        //如果有父类 检查父类
        var superClass = clazz.getSuperclass();
        if (superClass != null) return ReflectUtil.getFieldFormType(superClass,type);
        throw new NoSuchFieldException(type.getName());
    }

    //根据类型获取Field(针对泛型)
    @Deprecated(since = "不应该在这里的方法，已移动到ReflectUtil内")
    public static Field getFieldFormType(Class<?> clazz,String type) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getAnnotatedType().getType().getTypeName().equals(type)) return declaredField;
        }
        //如果有父类 检查父类
        var superClass = clazz.getSuperclass();
        if (superClass != null) return ReflectUtil.getFieldFormType(superClass,type);
        throw new NoSuchFieldException(type);
    }

    //从数组结构中查找Field
    @Deprecated(since = "不应该在这里的方法，已移动到ReflectUtil内")
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
