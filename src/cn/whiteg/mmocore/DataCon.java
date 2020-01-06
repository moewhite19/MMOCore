package cn.whiteg.mmocore;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataCon {
    private final File file;
    public boolean isNewFile = true;
    private boolean change = false;
    private String name;
    private UUID uuid;
    private FileConfiguration YMLFile = new YamlConfiguration();
    private boolean loaded = false;


    public DataCon(OfflinePlayer player) {
        uuid = player.getUniqueId();
        name = player.getName();
        file = new File(Setting.DATADIR,uuid.toString() + ".yml");
        if (name == null || name.isEmpty()) return;
        load(true);
    }

    public DataCon(Player player) {
        this(((OfflinePlayer) player));
    }

    public DataCon(UUID uuid,boolean load) {
        file = new File(Setting.DATADIR,uuid.toString() + ".yml");
        this.uuid = uuid;
        if (load){
            load(false);
        }
    }

    public DataCon(@NotNull String name,boolean load,boolean create) {
        this.name = name;
        uuid = MMOCore.getUUID(name);
        file = new File(Setting.DATADIR,uuid.toString() + ".yml");
        if (load){
            load(create);
        }
    }

    public boolean hasFile() {
        return file.exists();
    }

    public boolean load(boolean create) {
        if (file.exists()){
            YMLFile = YamlConfiguration.loadConfiguration(file);
            loaded = true;
            isNewFile = false;
            if (name == null){
                YMLFile.getString("Player.name","Null");
            }
//            else {
//                name = YMLFile.getString("Player.name",name);
//            }
            //uuid = MMOCore.getUUID(name);
//            if (uuid != null){
//                YMLFile.set("Player.uuid",uuid.toString());
//            }
            return true;
        } else if (create){
            YMLFile = new YamlConfiguration();
            YMLFile.set("Player.name",name);
            YMLFile.set("Player.uuid",uuid.toString());
            YMLFile.set("Player.join_time",String.valueOf(System.currentTimeMillis()));
            loaded = true;
            return true;
        }
        return false;
    }

    //储存
    @Deprecated
    public boolean Save() {
        return Save(true);
    }

    @Deprecated
    public boolean Save(boolean check) {
        if (isNewFile) return false;
        if (check){
            if (!IOcheck()){
                return false;
            }
        }
        try{
            YMLFile.save(file);
            return true;
        }catch (Exception e){
            MMOCore.logger.warning("玩家 " + name + " 的数据储存失败 : ");
            e.printStackTrace();
            return false;
        }
    }

    public boolean save() {
        if (isNewFile) return false;
        try{
            YMLFile.save(file);
            return true;
        }catch (Exception e){
            MMOCore.logger.warning("玩家 " + name + " 的数据储存失败 : ");
            e.printStackTrace();
            return false;
        }
    }


    public boolean checkSave() {
        if (isNewFile) return false;
        if (!IOcheck()){
            return false;
        }
        return save();
    }

    //检查文件是否要保存
    public boolean IOcheck() {
        if (isNewFile){
            //     long qt = Long.parseLong(YMLFile.getString("Player.join_time",String.valueOf(0)));
/*            if (qt == 0 || (System.currentTimeMillis() - qt) < 300000){
                MMOCore.logger.info("没有保存配置文件" + file.toString());
                return false;
            }*/
            if (Setting.hookLoginPlugin){
                return false;
            }
            if (file == null) return false;
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdir();
            }
            try{
                file.createNewFile();
                MMOCore.logger.info("创建空的配置文件:" + file.toString());
                isNewFile = false;
                return true;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getString(String pat) {
        return YMLFile.getString(pat);
    }

    public String getString(String pat,String def) {
        return YMLFile.getString(pat,def);
    }

    public int getInt(String pat) {
        return YMLFile.getInt(pat);
    }

    public void setString(String pat,String v1) {
        YMLFile.set(pat,v1);
        change = true;
    }

    public Object get(String pat) {
        return YMLFile.get(pat);
    }

    public void set(String pat,Object v1) {
        YMLFile.set(pat,v1);
        change = true;
    }

    public boolean isSet(String pat) {
        return YMLFile.isSet(pat);
    }

    public double getDouble(String path) {
        return YMLFile.getDouble(path);
    }

    public boolean getBoolean(String path) {
        return YMLFile.getBoolean(path);
    }

    public FileConfiguration getConfig() {
        return YMLFile;
    }

    public ConfigurationSection getSection(String path) {
        return YMLFile.getConfigurationSection(path);
    }

    public ConfigurationSection createSection(String path) {
        onSet();
        return YMLFile.createSection(path);
    }

    public boolean isSection(String path) {
        return YMLFile.isConfigurationSection(path);
    }

    public boolean contarins(String path) {
        return YMLFile.contains(path);
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isChange() {
        return change;
    }

    public void onSet() {
        this.change = true;
    }

    public void unload() {
        loaded = false;
    }

    public File getFile() {
        return file;
    }

    public void setYMLFile(FileConfiguration YMLFile) {
        this.YMLFile = YMLFile;
    }
}
