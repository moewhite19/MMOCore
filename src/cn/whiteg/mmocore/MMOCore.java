package cn.whiteg.mmocore;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static cn.whiteg.mmocore.Setting.reload;


public class MMOCore extends JavaPlugin {
    public static Logger logger;
    public static MMOCore plugin;
    public MainCommand mainCommand;
    public UserDataCommand userDataCommand;
    public SubCommand subCommand;
    public Map<UUID, DataCon> PlayerDataMap = new ConcurrentHashMap<>();
    public Map<String, Listener> listenerMap = new HashMap<>();

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
        DataCon dc = getPlayerData(player.getName());
        if (dc != null) return dc;
        dc = new DataCon(player);
        dc.isNewFile = false;
        plugin.PlayerDataMap.put(dc.getUUID(),dc);
        return dc;
    }

    public static DataCon craftData(String naem) {
        DataCon dc = getPlayerData(naem);
        if (dc != null) return dc;
        dc = new DataCon(naem,true,true);
        plugin.PlayerDataMap.put(dc.getUUID(),dc);
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
            ar.add(entry.getValue().getName());
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

    public void regEven(Listener listener) {
        regEven(listener.getClass().getName(),listener);

    }

    public void regEven(String key,Listener listener) {
        logger.info("注册事件:" + key);
        listenerMap.put(key,listener);
        Bukkit.getPluginManager().registerEvents(listener,plugin);

    }

    public void unregEven() {
        for (Map.Entry<String, Listener> entry : listenerMap.entrySet()) {
            unregListener(entry.getValue());
        }
    }


    /**
     * 卸载事件
     *
     * @param Key "卸载"
     */
    public boolean unregEven(String Key) {
        Listener listenr = listenerMap.remove(Key);
        if (listenr == null){
            return false;
        }
        unregListener(listenr);
        return true;
    }

    public void unregListener(Listener listener) {
        //注销事件
        Class listenerClass = listener.getClass();
        try{
            for (Method method : listenerClass.getMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)){
                    Type[] tpyes = method.getGenericParameterTypes();
                    if (tpyes.length == 1){
                        Class<?> tc = Class.forName(tpyes[0].getTypeName());
                        Method tm = tc.getMethod("getHandlerList");
                        HandlerList handlerList = (HandlerList) tm.invoke(null);
                        handlerList.unregister(listener);
                    }
                }
            }
        }catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e){
            e.printStackTrace();
        }

        //调用类中的unreg()方法
        try{
            Method unreg = listenerClass.getDeclaredMethod("unreg");
            unreg.setAccessible(true);
            unreg.invoke(listener);
        }catch (Exception e){

        }
    }

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
            regEven(new PlayerJoin());
            regEven(new PlayerQuit());
            regEven(new WorldSaveListener());
        }
        if (Setting.FREQUENTLY) regEven(new SafeNumEven());
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

    public void onDisable() {
        FileMan.onSaveALL();
        PlayerDataMap.clear();
        unregEven();
        //注销注册玩家加入服务器事件
        listenerMap.clear();
        logger.info("插件已关闭");
    }
}
