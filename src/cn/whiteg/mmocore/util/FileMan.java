package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.Event.DataConClearRecovervEvent;
import cn.whiteg.mmocore.Event.DataConDeleteEvent;
import cn.whiteg.mmocore.Event.DataConRecovervEvent;
import cn.whiteg.mmocore.Event.DataConRenameEvent;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;

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
            synchronized (MMOCore.getPlayerDataMap()) {
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
                    //怕了线程陷入死循环
                    break;
                }
            }
        }
//        for (Map.Entry entry : MMOCore.plugin.PlayerDataMap.entrySet()) {
//            DataCon pf = (DataCon) entry.getValue();
//            if (pf == null) continue;
//            pf.save();
//        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void recovery(CommandSender sender,String name) {
        UUID uuid = MMOCore.getUUID(name);
        Player p = Bukkit.getPlayerExact(name);
        if (p != null){
            MMOCore.getPlayerDataMap().remove(uuid);
        }
        File file = new File(Setting.RecoveryDir + File.separator + "playerdata",name + ".dat");
        File nDir = new File("world","playerdata");
        File nFile = new File(nDir,uuid.toString() + ".dat");
        if (file.exists()){
            if (!nDir.exists()){
                nDir.mkdirs();
            }
            if (nFile.exists()) nFile.delete();
            file.renameTo(nFile);
            sender.sendMessage("已恢复玩家存档");
        }
        file = new File(Setting.RecoveryDir + File.separator + "MMOCore",name + ".yml");
        nDir = Setting.DataDir;
        nFile = new File(nDir,uuid.toString() + ".yml");
        if (file.exists()){
            if (!nDir.exists()){
                nDir.mkdirs();
            }
            if (nFile.exists()) nFile.delete();
            file.renameTo(nFile);
            sender.sendMessage("已恢复数据");
        }
        file = new File(Setting.RecoveryDir + File.separator + "advancements",name + ".json");
        nDir = new File("world","advancements");
        nFile = new File(nDir,uuid.toString() + ".json");
        if (file.exists()){
            if (!nDir.exists()){
                nDir.mkdirs();
            }
            if (nFile.exists()) nFile.delete();
            file.renameTo(nFile);
            sender.sendMessage("已恢复进度");
        }
        DataConRecovervEvent event = new DataConRecovervEvent(sender,name);
        event.call();
        sender.sendMessage("已从回收站恢复数据");
    }

    public static void clearUpRecovery(CommandSender sender,int day) {
        long mintime = System.currentTimeMillis() - (day * 86400000);
        if (Setting.RecoveryDir.isDirectory()){
            int i = 0;
            for (File file : Setting.RecoveryDir.listFiles()) {
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
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

    @SuppressWarnings({"ResultOfMethodCallIgnored","SynchronizationOnLocalVariableOrMethodParameter"})
    public static void delete(CommandSender sender,DataCon dc) {
        final UUID uuid = dc.getUUID();
        Runnable runnable = () -> {
            DataConDeleteEvent e = new DataConDeleteEvent(dc,sender);
            e.call();
            if (e.isCancelled()) return;
            CommandSender commandSender = sender;
            if (sender == null) commandSender = Bukkit.getConsoleSender();
            final Player player = Bukkit.getPlayerExact(dc.getName());
            Map<UUID, DataCon> playerDataMap = MMOCore.getPlayerDataMap();
            synchronized (playerDataMap) {
                MMOCore.getPlayerDataMap().remove(uuid);
                dc.save();
                dc.unload();
            }

            if (player != null && player.isOnline()){
                player.kickPlayer("你被请出服务器");
                MMOCore.logger.warning("玩家 " + dc.getName() + " 当前在线，正在尝试重新删除");
                Bukkit.getScheduler().runTaskLater(MMOCore.plugin,() -> {
                    delete(sender,dc);
                },2L);
            }


            File file = new File("world/playerdata",uuid.toString() + ".dat");
            File nDir = new File(Setting.RecoveryDir + File.separator + "playerdata");
            File nFile = new File(nDir,dc.getName() + ".dat");
            if (file.exists()){
                if (!nDir.exists()) nDir.mkdirs();
                if (nFile.exists()){
                    file.delete();
                    commandSender.sendMessage("回收站数据已存在，已删除玩家数据");
                } else {
                    file.renameTo(nFile);
                    commandSender.sendMessage("已将玩家数据移动到回收站");
                }
            }


            file = new File(Setting.DataDir,uuid.toString() + ".yml");
            nDir = new File(Setting.RecoveryDir,"MMOCore");
            nFile = new File(nDir,dc.getName() + ".yml");
            if (file.exists()){
                if (!nDir.exists()) nDir.mkdirs();
                if (nFile.exists()){
                    file.delete();
                    commandSender.sendMessage("回收站数据已存在，已删除插件数据");
                } else {
                    file.renameTo(nFile);
                    commandSender.sendMessage("已将插件数据移动到回收站");
                }
            }


            file = new File("world/advancements",uuid.toString() + ".json");
            nDir = new File(Setting.RecoveryDir,"advancements");
            nFile = new File(nDir,dc.getName() + ".json");

            if (file.exists()){
                if (!nDir.exists()) nDir.mkdirs();
                if (nFile.exists()){
                    file.delete();
                    commandSender.sendMessage("回收站进度已存在，已删除玩家进度");
                } else {
                    file.renameTo(nFile);
                    commandSender.sendMessage("已将玩家进度移动到回收站");
                }
            }

        };

//        if (player != null && player.isOnline()){
//            player.kickPlayer("你被请出服务器");
//            Bukkit.getScheduler().runTaskLater(MMOCore.plugin,() -> {
//                MMOCore.unLoad(player.getUniqueId());
//            },2);
//            Bukkit.getScheduler().runTaskLater(MMOCore.plugin,runnable,5L);
//        } else {
//            runnable.run();
//        }
        if (Bukkit.isPrimaryThread()) runnable.run();
        else Bukkit.getScheduler().runTask(MMOCore.plugin,runnable);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void rename(CommandSender sender,DataCon dc,String name,String newName) {
        if (!CommonUtils.checkName(newName)){
            sender.sendMessage("无效名称");
            return;
        }
        if (dc == null){
            sender.sendMessage("找不到玩家");
            return;
        }
        Runnable runnable = () -> {
            try{
                if (Setting.onlineMode){
                    DataConRenameEvent event = new DataConRenameEvent(dc,name,newName,sender);
                    event.call();
                    if (event.isCancelled()) return;
                    dc.setName(newName);
                    sender.sendMessage("给在线玩家重命名完成");
                } else {
                    if (Bukkit.getPlayerExact(newName) != null){
                        sender.sendMessage("错误: 当前新ID是在线状态");
                        return;
                    }

                    Player player = dc.getPlayer();
                    //如果玩家在线
                    if (player != null && player.isOnline()){
                        MMOCore.logger.warning("玩家重命名时在线,正在尝试重新删除旧账户");
                        player.kickPlayer("正在帮你重命名");
                        Bukkit.getScheduler().runTaskLater(MMOCore.plugin,() -> {
                            delete(sender,dc);
                        },2);
                    }
                    DataCon newDc = MMOCore.craftData(newName);
                    if (!newDc.isLoaded()){
                        sender.sendMessage("创建新插件数据失败");
                        return;
                    }
                    DataConRenameEvent event = new DataConRenameEvent(dc,name,newName,sender);
                    event.call();
                    if (event.isCancelled()) return;
                    sender.sendMessage("创建新插件数据");
                    //转移插件数据
                    for (String key : dc.getConfig().getKeys(true)) {
                        if (newDc.isSet(key)) continue;
                        newDc.set(key,dc.get(key));
                    }
                    newDc.save();
                    UUID uuid = dc.getUUID();
                    UUID newuuid = newDc.getUUID();

                    MMOCore.unLoad(uuid);

                    File dir = new File("world/playerdata");
                    File file = new File(dir,uuid.toString() + ".dat");
                    File newFile = new File(dir,newuuid.toString() + ".dat");
                    if (file.exists()){
                        if (newFile.exists()) newFile.delete();
                        file.renameTo(newFile);
                        sender.sendMessage("以移动玩家数据");
                    }
                    file = new File(Setting.DataDir,uuid.toString() + ".yml");
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
                }

            }catch (Exception e){
                sender.sendMessage("重命名过程中出现异常" + e.getMessage());
                e.printStackTrace();
            }
        };
        if (Bukkit.isPrimaryThread()) runnable.run();
        else Bukkit.getScheduler().runTask(MMOCore.plugin,runnable);
    }


    public static void loadList(File file,List<String> list) {
        if (file.exists()){
            try{
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String f;
                while ((f = br.readLine()) != null) {
                    if (f.isEmpty()) continue;
                    try{
                        list.add(f);
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
                br.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void saveList(File file,List<String> list) {
        try{
            if (!file.exists()){
                File p = file.getParentFile();
                if (!p.exists()){
                    p.mkdirs();
                }
                file.createNewFile();
            }
            //1、打开流
            Writer w = new FileWriter(file);
            Iterator<String> i = list.iterator();
            //2、写入内容
            while (i.hasNext()) {
                w.write(new StringBuilder(i.next()).append('\n').toString());
            }
            //3、关闭流
            w.close();
        }catch (IOException e){
            System.out.println("文件写入错误：" + e.getMessage());
        }
    }

}
