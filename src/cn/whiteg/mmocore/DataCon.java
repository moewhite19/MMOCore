package cn.whiteg.mmocore;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
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
    final private File file;
    private boolean change = false;
    private String name;
    private UUID uuid;
    private FileConfiguration YMLFile = null;
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
            init();
            try{
                file.createNewFile();
                change = true;
                save();
                loaded = true;
                return true;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public void init() {
        if (YMLFile == null) YMLFile = new YamlConfiguration();
        YMLFile.set("Player.name",name);
        YMLFile.set("Player.uuid",uuid.toString());
        YMLFile.set("Player.join_time",String.valueOf(System.currentTimeMillis()));
    }


    public void unload() {
//        if (!loaded) return;
        loaded = false;
//        YMLFile = new YamlConfiguration();
    }

    public boolean save() {
        if (!change || !loaded) return false;
        try{
            if (file.exists()){
                if (Setting.DEBUG){
                    MMOCore.logger.info("储存玩家" + getName());
                }
                YMLFile.save(file);
                change = false;
                return true;
            }
            return false;
        }catch (Exception e){
            MMOCore.logger.warning("玩家 " + name + " 的数据储存失败 : ");
            e.printStackTrace();
            return false;
        }
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

    @Nullable
    public File getFile() {
        return file;
    }

    public void setYMLFile(FileConfiguration YMLFile) {
        this.YMLFile = YMLFile;
    }
}
