package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.reflection.ReflectUtil;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NMSUtils {
    private static Field craftEntity;

    private static String craftRoot;

    static Map<Class<? extends Entity>, EntityType<?>> entityClassTypeMap = new HashMap<>();
    static Map<Class<? extends BlockEntity>, BlockEntityType<?>> tileClassEntityMap = new HashMap<>();
    static Map<EntityType<?>, Class<? extends Entity>> entityTypeClassMap = new HashMap<>();

    static {

        try{
            craftRoot = Bukkit.getServer().getClass().getPackage().getName();
            //从Bukkit实体获取Nms实体
            var clazz = EntityUtils.class.getClassLoader().loadClass(craftRoot + ".entity.CraftEntity");
            craftEntity = ReflectUtil.getFieldFormType(clazz,Entity.class);
            craftEntity.setAccessible(true);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //根据类型获取Field
    @Deprecated(since = "不应该在这里的方法，已移动到ReflectUtil内", forRemoval = true)
    public static Field getFieldFormType(Class<?> clazz,Class<?> type) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getType().equals(type)) return declaredField;
        }

        //如果有父类 检查父类
        var superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) return ReflectUtil.getFieldFormType(superClass,type);
        throw new NoSuchFieldException(type.getName());
    }

    //根据类型获取Field(针对泛型)
    @Deprecated(since = "不应该在这里的方法，已移动到ReflectUtil内", forRemoval = true)
    public static Field getFieldFormType(Class<?> clazz,String type) throws NoSuchFieldException {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.getAnnotatedType().getType().getTypeName().equals(type)) return declaredField;
        }
        //如果有父类 检查父类
        var superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) return ReflectUtil.getFieldFormType(superClass,type);
        throw new NoSuchFieldException(type);
    }

    //从数组结构中查找Field
    @Deprecated(since = "不应该在这里的方法，已移动到ReflectUtil内", forRemoval = true)
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

    /**
     * @noinspection unchecked
     */ //根据实体Class获取实体Types
    public static <T extends Entity> EntityType<T> getEntityType(Class<T> clazz) {

        return (EntityType<T>) entityClassTypeMap.computeIfAbsent(clazz,entityClass -> {
            String name = EntityType.class.getName().concat("<").concat(clazz.getName()).concat(">");
            for (Field field : EntityType.class.getFields()) {
                if (!Modifier.isStatic(field.getModifiers())) continue; //跳过非静态Field
                try{
                    if (field.getAnnotatedType().getType().getTypeName().equals(name))
                        //noinspection unchecked
                        return (EntityType<T>) field.get(null);
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
            MMOCore.logger.warning("无法根据Class找到Entity: " + clazz);
            return null;
        });
    }

    /**
     * @noinspection unchecked
     */ //根据实体Class获取Tile方块实体Types
    public static <T extends BlockEntity> BlockEntityType<T> getTileEntityType(Class<T> clazz) {
        return (BlockEntityType<T>) tileClassEntityMap.computeIfAbsent(clazz,aClass -> {
            String name = BlockEntityType.class.getName().concat("<").concat(clazz.getName()).concat(">");
            for (Field field : BlockEntityType.class.getFields()) {
                if (!Modifier.isStatic(field.getModifiers())) continue; //跳过非静态Field
                try{
                    if (field.getAnnotatedType().getType().getTypeName().equals(name))
                        //noinspection unchecked
                        return (BlockEntityType<T>) field.get(null);
                }catch (IllegalAccessException e){
                    e.printStackTrace();
                }
            }
            MMOCore.logger.warning("无法根据Class找到BlockEntity: " + clazz);
            return null;
        });
    }

    /**
     * @noinspection unchecked
     */ //根据实体Type获取实体Class
    public static <T extends Entity> Class<? extends Entity> getEntityClass(EntityType<T> type) {

        return entityTypeClassMap.computeIfAbsent(type,entityClass -> {
            for (Field field : EntityType.class.getFields()) {
                if (!Modifier.isStatic(field.getModifiers())) continue; //跳过非静态Field
                try{
                    final Object getType = field.get(null);
                    if (getType.equals(type)){
                        final String typeName = field.getAnnotatedType().getType().getTypeName();
                        return (Class<? extends Entity>) NMSUtils.class.getClassLoader().loadClass(typeName.substring(typeName.indexOf("<") + 1,typeName.length() - 1));
                    }
                }catch (IllegalAccessException | ClassNotFoundException e){
                    e.printStackTrace();
                }
            }
            MMOCore.logger.warning("无法根据Type找到类: " + type);
            return null;
        });
    }

    public static Entity getNmsEntity(org.bukkit.entity.Entity entity) {
        try{
            return (Entity) craftEntity.get(entity);
        }catch (IllegalAccessException e){
            throw new RuntimeException(e);
        }
    }

    public static ServerLevel getNmsWorld(World world) {
        return ((CraftWorld) world).getHandle();
    }

    public static DedicatedServer getNmsServer() {
        return ((CraftServer) Bukkit.getServer()).getServer();
    }
}
