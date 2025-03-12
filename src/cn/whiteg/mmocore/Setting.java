package cn.whiteg.mmocore;

import cn.whiteg.mmocore.sound.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Setting {
    public final static int VER = 6;
    public static boolean DEBUG;
    public static FileConfiguration config;
    public static int Frequent;
    public static boolean DELETE_CACHE;
    public static boolean FREQUENTLY;
    public static boolean onlineMode;
    public static int LatelyPlayerListSize;
    public static Sound PlayerReqestSound = Sound.EMPTY;
    public static File DataDir;
    public static File RecoveryDir;
    public static int MaxRecovery;

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
                if (config.contains(k)) continue;
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
        FREQUENTLY = config.getBoolean("Frequently",false);

        PlayerReqestSound = Sound.parseYml(config.get("PlayerReqestSound"));

        LatelyPlayerListSize = config.getInt("LatelyPlayerListSize",50);

        onlineMode = config.getBoolean("Online_Mode",false);

        MaxRecovery = config.getInt("Max_Recovery",300);

        String dir = config.getString("DataDir");
        if (dir != null){
            DataDir = new File(dir);
        } else {
            DataDir = new File(MMOCore.plugin.getDataFolder(),"players");
        }
        RecoveryDir = new File(DataDir.getParentFile(),"recovery");
    }
}
