package cn.whiteg.mmocore;

import cn.whiteg.mmocore.event.DataConCreateEvent;
import cn.whiteg.mmocore.event.DataConRenameEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class DataCon {
    public static final String NAME_KEY = "Player.name";
    public static final String UUID_KEY = "Player.uuid";
    public static final String JOIN_KEY = "Player.join_time";

    //    public static final UUID zeroUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    final private File file;
    private UUID uuid;
    private String name;
    private boolean change = false;
    private FileConfiguration YMLFile = null;
    private boolean loaded = false;


    public DataCon(OfflinePlayer player) {
        uuid = player.getUniqueId();
        name = player.getName();
        file = new File(Setting.DataDir,uuid.toString() + ".yml");
        if (name == null || name.isEmpty()) return;
        load(true);
    }

    public DataCon(UUID uuid,boolean load) {
        file = new File(Setting.DataDir,uuid.toString() + ".yml");
        this.uuid = uuid;
        if (load){
            load(false);
        }
    }

    public DataCon(String name,boolean load,boolean create) {
        this.name = name;
        uuid = MMOCore.getUUID(name);
        file = new File(Setting.DataDir,uuid.toString() + ".yml");
        if (load){
            load(create);
        }
    }

    public DataCon(File file) {
        this.file = file;
        load(false);
    }

    public boolean hasFile() {
        return file.exists();
    }

    public boolean load(boolean create) {
        if (file.exists()){
            YMLFile = YamlConfiguration.loadConfiguration(file);
            loaded = true;
            String data;
            data = YMLFile.getString(NAME_KEY,"Null");
            if (name == null){
                name = data;
            } else if (!name.equals(data)){
                YMLFile.set(NAME_KEY,name);
                onSet();
            }
            data = YMLFile.getString(UUID_KEY);
            if (uuid == null){
                uuid = MMOCore.getUUID(name);
            } else if (!uuid.toString().equals(data)){
                YMLFile.set(UUID_KEY,uuid.toString());
                onSet();
            }
            save();
            return true;
        } else if (create){
            init();
            try{
                File dir = file.getParentFile();
                if (!dir.exists()) dir.mkdirs();
                file.createNewFile();
                change = true;
                save();
                loaded = true;
                //调用事件
                var event = new DataConCreateEvent(this);
                event.call();
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public void init() {
        if (YMLFile == null) YMLFile = new YamlConfiguration();
        YMLFile.set(NAME_KEY,name);
        YMLFile.set(UUID_KEY,uuid.toString());
        YMLFile.set(JOIN_KEY,System.currentTimeMillis());
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

    public void setName(String name) {
        this.name = name;
        YMLFile.set("Player.name",name);
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

    public File getFile() {
        return file;
    }

    public void setYMLFile(FileConfiguration YMLFile) {
        this.YMLFile = YMLFile;
    }

    public void update(Player player) {
        String n = YMLFile.getString("Player.name");
        String playerName = player.getName();
        if (n == null){
            setName(playerName);
        } else if (!n.equals(playerName)){
            CommandSender sender = Bukkit.getConsoleSender();
            sender.sendMessage("§b玩家§f" + n + "§b已重命名为§f" + playerName);
            DataConRenameEvent event = new DataConRenameEvent(this,n,playerName,Bukkit.getConsoleSender());
            event.call();
            if (event.isCancelled()) return;
            setName(playerName);
        }
    }
}
