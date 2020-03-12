package cn.whiteg.mmocore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Setting {
    public final static int VER = 3;
    public static boolean DEBUG;
    public static FileConfiguration config;
    public static int Frequent;
    public static boolean DELETE_CACHE;
    public static boolean SAVE_PLAYERDATA;
    public static boolean FREQUENTLY;
    public static File DATADIR;

    public static void reload() {
        File file = new File(MMOCore.plugin.getDataFolder(),"config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        if (config.getInt("ver") != VER){
            MMOCore.logger.info("更新配置文件");
            MMOCore.plugin.saveResource("config.yml",true);
            config.set("ver",VER);
            final FileConfiguration newcon = YamlConfiguration.loadConfiguration(file);
            Set<String> keys = newcon.getKeys(true);
            for (String k : keys) {
                if (config.isSet(k)) continue;
                config.set(k,newcon.get(k));
                MMOCore.logger.info("在配置文件新增值: " + k);
            }
            try{
                config.save(file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        Frequent = 1000;
        DEBUG = config.getBoolean("debug");
        DELETE_CACHE = config.getBoolean("QuitDeleteCache",false);
        SAVE_PLAYERDATA = config.getBoolean("SavePlayerData",false);
        FREQUENTLY = config.getBoolean("Frequently",false);
        String dir = config.getString("DataDir");
        if (dir != null){
            DATADIR = new File(dir);
        } else {
            DATADIR = new File(MMOCore.plugin.getDataFolder(),"players");
        }
    }
}
