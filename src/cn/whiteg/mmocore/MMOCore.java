package cn.whiteg.mmocore;

import cn.whiteg.mmocore.common.CommandManage;
import cn.whiteg.mmocore.common.PluginBase;
import cn.whiteg.mmocore.listener.MMOCoreListener;
import cn.whiteg.mmocore.listener.SafeNumEven;
import cn.whiteg.mmocore.listener.WorldSaveListener;
import cn.whiteg.mmocore.util.DataIterator;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

import static cn.whiteg.mmocore.Setting.reload;


public class MMOCore extends PluginBase {
    public static Logger logger;
    public static MMOCore plugin;
    public static LinkedList<String> latelyPlayerList = new LinkedList<>(); //最近玩家列表
    private final Map<UUID, DataCon> PlayerDataMap = Collections.synchronizedMap(new HashMap<>());
    public CommandManage mainCommand;
    public CommandManage userDataCommand;

    public MMOCore() {
        plugin = this;
    }

    //c 这些是当初跳过Java基础写的Bukkit
    //  现在回顾都是shi代码x，反正恰好能完成他的工作,有空再改吧
    public static DataCon getPlayerData(Player player) {
        return getPlayerData(((OfflinePlayer) player));
    }

    public static DataCon getPlayerData(OfflinePlayer player) {
        synchronized (plugin.PlayerDataMap) {
            DataCon dc = plugin.PlayerDataMap.get(player.getUniqueId());
            if (dc != null){
                return dc;
            }
            dc = new DataCon(player);
            if (!dc.isLoaded()) return null;
            plugin.PlayerDataMap.put(player.getUniqueId(),dc);
            return dc;
        }
    }

    public static DataCon getPlayerData(UUID uuid) {
        return getPlayerData(uuid,true);
    }

    public static DataCon getPlayerData(String name,boolean cache) {
        synchronized (plugin.PlayerDataMap) {
            DataCon dc = plugin.PlayerDataMap.get(getUUID(name));
            if (dc != null) return dc;
            dc = new DataCon(name,true,false);
            if (dc.isLoaded()){
                if (cache) plugin.PlayerDataMap.put(dc.getUUID(),dc);
                return dc;
            }
            return null;
        }
    }

    public static DataCon getPlayerData(String name) {
        return getPlayerData(name,true);
    }

    public static DataCon getPlayerData(UUID uuid,boolean cache) {
        synchronized (plugin.PlayerDataMap) {
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
    }

    public static Iterator<DataCon> iteratorPlayerData() {
        File[] files = Setting.DataDir.listFiles();
        return new DataIterator(files);
    }

    public static DataCon getPlayerData(CommandSender sender) {
        if (sender instanceof OfflinePlayer) return getPlayerData(((OfflinePlayer) sender));
        return getPlayerData(sender.getName());
    }

    public static boolean hasPlayerData(String name) {
        return hasPlayerData(getUUID(name));
    }

    public static boolean hasPlayerData(UUID uuid) {
        synchronized (plugin.PlayerDataMap) {
            if (plugin.PlayerDataMap.containsKey(uuid)){
                return true;
            }
        }
        final File file = new File(Setting.DataDir,uuid.toString() + ".yml");
        return file.exists();
    }

    public static DataCon craftData(Player player) {
        synchronized (plugin.PlayerDataMap) {
            DataCon dc = plugin.PlayerDataMap.get(player.getUniqueId());
            if (dc != null) return dc;
            dc = new DataCon(player);
            if (dc.isLoaded()) plugin.PlayerDataMap.put(dc.getUUID(),dc);
            return dc;
        }
    }

    public static DataCon craftData(String name) {
        synchronized (plugin.PlayerDataMap) {
            DataCon dc = plugin.PlayerDataMap.get(getUUID(name));
            if (dc != null) return dc;
            dc = new DataCon(name,true,true);
            if (dc.isLoaded()) plugin.PlayerDataMap.put(dc.getUUID(),dc);
            return dc;
        }
    }

    public static Map<UUID, DataCon> getPlayerDataMap() {
        return plugin.PlayerDataMap;
    }


    public static DataCon unLoad(UUID uuid) {
        synchronized (plugin.PlayerDataMap) {
            if (Setting.DEBUG){
                logger.info("Unload " + uuid);
            }
            DataCon dc = plugin.PlayerDataMap.remove(uuid);
            if (dc != null) dc.unload();
            return dc;
        }
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
        if (Setting.onlineMode){
            Player player = Bukkit.getPlayerExact(name);
            if (player != null){
                return player.getUniqueId();
            }
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            return op.getUniqueId();
        }
        return getOfflineUUID(name);
    }

    public static List<String> getLatelyPlayerList() {
        return latelyPlayerList;
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

        mainCommand = new CommandManage(this,"cn/whiteg/mmocore/commands/mainCommand/");
        mainCommand.setExecutor();

        userDataCommand = new CommandManage(this,"cn/whiteg/mmocore/commands/userDataCommands/");
        userDataCommand.setExecutor("userdata");

        regListener(new MMOCoreListener());
        regListener(new WorldSaveListener());
        if (Setting.FREQUENTLY) regListener(new SafeNumEven());
        logger.info("全部加载完成");
        for (Player player : Bukkit.getOnlinePlayers()) {
            FileMan.load(player);
        }

        //从文件加载最近玩家列表
        FileMan.loadList(new File(getDataFolder(),"latelyPlayerList.txt"),latelyPlayerList);
    }

    @Override
    public void onDisable() {
        FileMan.onSaveALL();
        PlayerDataMap.clear();
        //注销注册玩家加入服务器事件
        unregListener();
        //储存最近玩家列表
        FileMan.saveList(new File(getDataFolder(),"latelyPlayerList.txt"),latelyPlayerList);
        logger.info("插件已关闭");
    }

}
