package cn.whiteg.mmocore;

import cn.whiteg.mmocore.common.PluginBase;
import cn.whiteg.mmocore.listener.PlayerJoin;
import cn.whiteg.mmocore.listener.PlayerQuit;
import cn.whiteg.mmocore.listener.SafeNumEven;
import cn.whiteg.mmocore.listener.WorldSaveListener;
import cn.whiteg.mmocore.util.FileMan;
import cn.whiteg.mmocore.util.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static cn.whiteg.mmocore.Setting.reload;


public class MMOCore extends PluginBase {
    public static Logger logger;
    public static MMOCore plugin;
    public MainCommand mainCommand;
    public UserDataCommand userDataCommand;
    public SubCommand subCommand;
    public Map<UUID, DataCon> PlayerDataMap = new ConcurrentHashMap<>();

    public MMOCore() {
        plugin = this;
    }

    public static DataCon getPlayerData(Player player) {
        return getPlayerData(((OfflinePlayer) player));
    }

    public static DataCon getPlayerData(OfflinePlayer player) {
        DataCon dc = plugin.PlayerDataMap.get(player.getUniqueId());
        if (dc != null){
            return dc;
        }
        dc = new DataCon(player);
        plugin.PlayerDataMap.put(player.getUniqueId(),dc);

        return dc;
    }

    public static DataCon getPlayerData(UUID uuid) {
        return getPlayerData(uuid,true);
    }

    public static DataCon getPlayerData(String name,boolean cache) {
        DataCon dc = plugin.PlayerDataMap.get(getUUID(name));
        if (dc != null) return dc;
        dc = new DataCon(name,true,false);
        if (dc.isLoaded()){
            if (cache) plugin.PlayerDataMap.put(dc.getUUID(),dc);
            return dc;
        }
        return null;
    }

    public static DataCon getPlayerData(String name) {
        return getPlayerData(name,true);
    }

    public static DataCon getPlayerData(UUID uuid,boolean cache) {
        DataCon dc = plugin.PlayerDataMap.get(uuid);
        if (dc != null) return dc;
        dc = new DataCon(uuid,true);
        if (dc.isLoaded()){
            if (cache) plugin.PlayerDataMap.put(dc.getUUID(),dc);
            plugin.PlayerDataMap.put(uuid,dc);
            return dc;
        }
        return null;
    }

    public static boolean hasPlayerData(String name) {
        final String n = getUUID(name).toString();
        final File file = new File(Setting.DATADIR,n + ".yml");
        return file.exists();
    }

    public static boolean hasPlayerData(UUID uuid) {
        final File file = new File(Setting.DATADIR,uuid.toString() + ".yml");
        return file.exists();
    }

    public static DataCon craftData(Player player) {
        DataCon dc = plugin.PlayerDataMap.get(player.getUniqueId());
        if (dc != null) return dc;
        dc = new DataCon(player);
        return dc;
    }

    public static DataCon craftData(String name) {
        DataCon dc = plugin.PlayerDataMap.get(getUUID(name));
        if (dc != null) return dc;
        dc = new DataCon(name,true,true);
        return dc;
    }

    public static DataCon unLoad(UUID uuid) {
        if (Setting.DEBUG){
            logger.info("Unload " + uuid);
        }
        DataCon dc = plugin.PlayerDataMap.remove(uuid);
        if (dc != null) dc.unload();
        return dc;
    }

    public static List<String> getLoadDataNames() {
        List<String> ar = new ArrayList<>();
        for (Map.Entry<UUID, DataCon> entry : plugin.PlayerDataMap.entrySet()) {
            DataCon dc = entry.getValue();
            try{
                ar.add(dc.getName());
            }catch (Exception e){
                e.printStackTrace();
                ar.add(dc.getFile().getName());
            }
        }
        return ar;
    }

    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    public static UUID getUUID(String name) {
        if (Bukkit.getServer().getOnlineMode()){
            Player player = Bukkit.getPlayerExact(name);
            if (player != null){
                if (player.getName().equals(name)) return player.getUniqueId();
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            return op.getUniqueId();
        }
        return getOfflineUUID(name);
    }

    public void onLoad() {
        saveDefaultConfig();
    }

    public void onReload() {
        logger.info("--开始重载--");
        reload();
        logger.info("--重载完成--");
    }

    @Override
    public void onEnable() {
        plugin = this;
        logger = getLogger();
        logger.info("开始加载插件");
        reload();
        if (Setting.DEBUG) logger.info("§a调试模式已开启");
        mainCommand = new MainCommand();
        getCommand("MMOCore").setExecutor(mainCommand);
        userDataCommand = new UserDataCommand();
        getCommand("userdata").setExecutor(userDataCommand);
        subCommand = new SubCommand();
        if (Setting.SAVE_PLAYERDATA){
            regListener(new PlayerJoin());
            regListener(new PlayerQuit());
            regListener(new WorldSaveListener());
        }
        if (Setting.FREQUENTLY) regListener(new SafeNumEven());
        logger.info("全部加载完成");
        for (Player player : Bukkit.getOnlinePlayers()) {
            FileMan.load(player);
        }
        Bukkit.getScheduler().runTask(this,() -> {
            for (String name : subCommand.subCmds) {
                PluginCommand pc = PluginUtil.getPluginCommanc(this,name);
                if (pc != null){
                    pc.setExecutor(subCommand);
                    pc.setTabCompleter(subCommand);
                } else {
                    logger.warning("指令 " + name + " 注册失败");
                }
            }
        });
    }

    @Override
    public void onDisable() {
        FileMan.onSaveALL();
        PlayerDataMap.clear();
        unregListener();
        //注销注册玩家加入服务器事件
        logger.info("插件已关闭");
    }
}
