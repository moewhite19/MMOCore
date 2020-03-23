package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.Event.DataConClearRecovervEvent;
import cn.whiteg.mmocore.Event.DataConDeleteEvent;
import cn.whiteg.mmocore.Event.DataConRecovervEvent;
import cn.whiteg.mmocore.Event.DataConRenameEvent;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import cn.whiteg.moeLogin.utils.PasswordUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FileMan {

    public static void load(Player player) {
        synchronized (MMOCore.getPlayerDataMap()) {
            if (MMOCore.getPlayerDataMap().containsKey(player.getUniqueId())) return;
            DataCon dc = new DataCon(player);
            MMOCore.getPlayerDataMap().put(player.getUniqueId(),dc);
        }
    }

    public static void SchedulSaveAll() {
        Bukkit.getScheduler().runTaskAsynchronously(MMOCore.plugin,FileMan::onSaveALL);
    }

    public static void onSaveALL() {
        final Iterator<Map.Entry<UUID, DataCon>> it = MMOCore.getPlayerDataMap().entrySet().iterator();
        while (it.hasNext()) {
            try{
                final DataCon dc = it.next().getValue();
                if (dc == null){
                    it.remove();
                    continue;
                }
                Set<String> keys = dc.getConfig().getKeys(false);
                if (keys.isEmpty()){
                    if (Setting.DEBUG){
                        MMOCore.logger.info("清理空配置" + dc.getName());
                    }
                    it.remove();
                    continue;
                }
                dc.save();
                Player player = dc.getPlayer();
                if (player == null || !player.isOnline()){
                    if (Setting.DEBUG){
                        MMOCore.logger.info("清理过时离线玩家" + dc.getName());
                    }
                    it.remove();
                    dc.unload();
                }
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
//        for (Map.Entry entry : MMOCore.plugin.PlayerDataMap.entrySet()) {
//            DataCon pf = (DataCon) entry.getValue();
//            if (pf == null) continue;
//            pf.save();
//        }
    }

    public static void recovery(CommandSender sender,String name) {
        UUID uuid = MMOCore.getUUID(name);
        Player p = Bukkit.getPlayerExact(name);
        if (p != null){
            MMOCore.getPlayerDataMap().remove(uuid);
        }
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery");
        File file = new File(recoveryDir + File.separator + "playerdata",name + ".dat");
        File nDir = new File("world","playerdata");
        File nFile = new File(nDir,uuid.toString() + ".dat");
        if (file.exists()){
            if (!nDir.exists()){
                nDir.mkdirs();
            }
            file.renameTo(nFile);
        }
        file = new File(recoveryDir + File.separator + "MMOCore",name + ".yml");
        nDir = new File("plugins/MMOCore/Player");
        nFile = new File(nDir,uuid.toString() + ".yml");
        if (file.exists()){
            if (!nDir.exists()){
                nDir.mkdirs();
            }
            file.renameTo(nFile);
        }
        file = new File(recoveryDir + File.separator + "advancements",name + ".json");
        nDir = new File("world","advancements");
        nFile = new File(nDir,uuid.toString() + ".json");
        if (file.exists()){
            if (!nDir.exists()){
                nDir.mkdirs();
            }
            file.renameTo(nFile);
        }
        DataConRecovervEvent event = new DataConRecovervEvent(sender,name);
        event.call();
        sender.sendMessage("已从回收站恢复数据");
    }

    public static void clearUpRecovery(CommandSender sender,int day) {
        long mintime = System.currentTimeMillis() - (day * 86400000);
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery" + File.separator + "MMOCore");
        if (recoveryDir.isDirectory()){
            int i = 0;
            for (File file : recoveryDir.listFiles()) {
                if (file.lastModified() < mintime){
                    String name = file.getName();
                    int w = name.lastIndexOf('.');
                    if (w != -1){
                        name = name.substring(0,w);
                    }
                    deleteRecovery(sender,name);
                    i++;
                }
            }
            sender.sendMessage("§b共清理了§f" + i + "§b个回收站数据");
        }
    }

    public static void deleteRecovery(CommandSender sender,String name) {
        DataConClearRecovervEvent event = new DataConClearRecovervEvent(sender,name);
        event.call();
        if (event.isCancelled()) return;
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery");
        File file = new File(recoveryDir + File.separator + "playerdata",name + ".dat");
        if (file.exists()){
            file.delete();
//            sender.sendMessage("已删除回收站玩家存档");
        }
        file = new File(recoveryDir + File.separator + "MMOCore",name + ".yml");
        if (file.exists()){
            file.delete();
//            sender.sendMessage("已删除回收站玩家数据");
        }
        file = new File(recoveryDir + File.separator + "advancements",name + ".json");
        if (file.exists()){
            file.delete();
//            sender.sendMessage("已删除回收站玩家进度");
        }
        sender.sendMessage("已从回收站移除 " + name);
    }

    public static void clearRecovery() {
//        UUID uuid = MMOCore.getUUID(name);
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery");
        if (recoveryDir.exists()){
            recoveryDir.delete();
        }
    }

    public static String[] canRecoverys() {
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery" + File.separator + "MMOCore");
        if (recoveryDir.exists()){
            String[] files = recoveryDir.list();
            assert files != null;
            for (int i = 0; i < files.length; i++) {
                String f = files[i];
                int w = f.lastIndexOf('.');
                if (w != -1){
                    files[i] = f.substring(0,w);
                }
            }
            return files;
        }
        return new String[0];
    }

    public static boolean canRecovery(String name) {
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery" + File.separator + "MMOCore");
        if (recoveryDir.exists()){
            File file = new File(recoveryDir,name + ".yml");
            return file.exists();
        }
        return false;
    }

    public static void delete(CommandSender sender,DataCon dc) {
        DataConDeleteEvent e = new DataConDeleteEvent(dc,sender);
        e.call();
        if (e.isCancelled()) return;
        final Player p = Bukkit.getPlayerExact(dc.getName());
        if (p != null) p.kickPlayer("你被请出服务器");
        if (sender == null) sender = Bukkit.getConsoleSender();
        final UUID uuid = dc.getUUID();
        //if (uuid == null || uuid.isEmpty()) uuid = MMOCore.getOfflineUUID(args[1]).toString();
        MMOCore.getPlayerDataMap().remove(uuid);
        dc.save();
        dc.unload();
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery");
        File file = new File("world/playerdata",uuid.toString() + ".dat");
        File nDir = new File(recoveryDir + File.separator + "playerdata");
        File nFile = new File(nDir,dc.getName() + ".dat");
//        Logger logger = MMOCore.logger;
        if (file.exists()){
            if (!nDir.exists()) nDir.mkdirs();
            if (nFile.exists()) nFile.delete();
            file.renameTo(nFile);
            sender.sendMessage("已删除玩家存档");
        }
        file = new File("plugins/MMOCore/Player",uuid.toString() + ".yml");
        nDir = new File(recoveryDir,"MMOCore");
        nFile = new File(nDir,dc.getName() + ".yml");
        if (file.exists()){
            if (!nDir.exists()) nDir.mkdirs();
            if (nFile.exists()) nFile.delete();
            file.renameTo(nFile);
            sender.sendMessage("已删除玩家数据");
        }
        file = new File("world/advancements",uuid.toString() + ".json");
        nDir = new File(recoveryDir,"advancements");
        nFile = new File(nDir,dc.getName() + ".json");
        if (file.exists()){
            if (!nDir.exists()) nDir.mkdirs();
            if (nFile.exists()) nFile.delete();
            file.renameTo(nFile);
            sender.sendMessage("已删除玩家进度");
        }
    }

    public static void rename(CommandSender sender,DataCon dc,String newId) {
        if (!PasswordUtils.checkName(newId)){
            sender.sendMessage("无效名称");
        }
        if (dc == null){
            sender.sendMessage("找不到玩家");
        }
        Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
            try{
                if (Bukkit.getPlayerExact(newId) != null){
                    sender.sendMessage("错误: 当前新ID是在线状态");
                    return;
                }
                Player p = dc.getPlayer();
                if (p != null) p.kickPlayer("正在帮你重命名");
//                MMOCore.getPlayerDataMap().remove(dc.getUUID());
                dc.unload();
                DataCon newDc = MMOCore.craftData(newId);
                if (!newDc.isLoaded()){
                    sender.sendMessage("创建新插件数据失败");
                    return;
                }
                DataConRenameEvent event = new DataConRenameEvent(dc,newId,sender);
                event.call();
                if (event.isCancelled()) return;
                sender.sendMessage("创建新插件数据");
                for (String key : dc.getConfig().getKeys(true)) {
                    if (newDc.isSet(key)) continue;
                    newDc.set(key,dc.get(key));
                }
                newDc.save();
                UUID uuid = dc.getUUID();
                UUID newuuid = newDc.getUUID();
                //if (uuid == null || uuid.isEmpty()) uuid = MMOCore.getOfflineUUID(args[1]).toString();
                File dir = new File("world/playerdata");
                File file = new File(dir,uuid.toString() + ".dat");
                File newFile = new File(dir,newuuid.toString() + ".dat");
                if (file.exists()){
                    if (newFile.exists()) newFile.delete();
                    file.renameTo(newFile);
                    sender.sendMessage("以移动玩家数据");
                }
                file = new File("plugins/MMOCore/Player",uuid.toString() + ".yml");
                if (file.exists()){
                    file.delete();
                    sender.sendMessage("删除旧插件数据");
                }
                dir = new File("world/advancements");
                file = new File(dir,uuid.toString() + ".json");
                newFile = new File(dir,newuuid.toString() + ".json");
                if (file.exists()){
                    file.renameTo(newFile);
                    if (newFile.exists()) newFile.delete();
                    sender.sendMessage("已转移进度");
                }
                sender.sendMessage("重命名完成");
            }catch (Exception e){
                sender.sendMessage("重命名过程中出现异常" + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
