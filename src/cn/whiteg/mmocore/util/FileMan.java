package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.event.DataConClearRecovervEvent;
import cn.whiteg.mmocore.event.DataConDeleteEvent;
import cn.whiteg.mmocore.event.DataConRecovervEvent;
import cn.whiteg.mmocore.event.DataConRenameEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileMan {

    public static final String DONE = "§b完成";
    public static final String FAIL = "§c失败";

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
        File zipFile = new File(Setting.RecoveryDir,name + ".zip");
        if (zipFile.exists()){
            UUID uuid = MMOCore.getUUID(name);

            try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))){
                Player p = Bukkit.getPlayerExact(name);
                if (p != null){
                    MMOCore.getPlayerDataMap().remove(uuid);
                }
                ZipEntry entry;
                while ((entry = zipInputStream.getNextEntry()) != null) {
                    final String type = entry.getName();
                    if ("mmocore".equals(type)){
                        if (zipRead(zipInputStream,new File(Setting.DataDir,uuid + ".yml"))){
                            sender.sendMessage(" §b已从回收站恢复数据");
                        }
                    } else if ("playerdata".equals(type)){
                        if (zipRead(zipInputStream,new File("world" + File.separator + type + File.separator + uuid + ".dat"))){
                            sender.sendMessage(" §b已从回收站恢复玩家存档");
                        }
                    } else if ("advancements".equals(type) || "stats".equals(type)){
                        if (zipRead(zipInputStream,new File("world" + File.separator + type + File.separator + uuid + ".json"))){
                            sender.sendMessage(" §b已从回收站恢复§r" + type);
                        }
                    }
                }

                //重新加载玩家数据
                if (p != null){
                    p.loadData();
                }
            }catch (IOException e){
                sender.sendMessage(" 无法从回收站恢复: " + e.getMessage());
                return;
            }
            zipFile.delete();
//
//            File file = new File(Setting.RecoveryDir + File.separator + "playerdata",name + ".dat");
//            File nDir = new File("world","playerdata");
//            File nFile = new File(nDir,uuid.toString() + ".dat");
//            if (file.exists()){
//                if (!nDir.exists()){
//                    nDir.mkdirs();
//                }
//                if (nFile.exists()) nFile.delete();
//                file.renameTo(nFile);
//                sender.sendMessage("已恢复玩家存档");
//            }
//            file = new File(Setting.RecoveryDir + File.separator + "MMOCore",name + ".yml");
//            nDir = Setting.DataDir;
//            nFile = new File(nDir,uuid.toString() + ".yml");
//            if (file.exists()){
//                if (!nDir.exists()){
//                    nDir.mkdirs();
//                }
//                if (nFile.exists()) nFile.delete();
//                file.renameTo(nFile);
//                sender.sendMessage("已恢复数据");
//            }
//            file = new File(Setting.RecoveryDir + File.separator + "advancements",name + ".json");
//            nDir = new File("world","advancements");
//            nFile = new File(nDir,uuid.toString() + ".json");
//            if (file.exists()){
//                if (!nDir.exists()){
//                    nDir.mkdirs();
//                }
//                if (nFile.exists()) nFile.delete();
//                file.renameTo(nFile);
//                sender.sendMessage("已恢复进度");
//            }
            DataConRecovervEvent event = new DataConRecovervEvent(sender,name);
            event.call();
            sender.sendMessage(" 已从回收站恢复数据");
        } else {
            sender.sendMessage(" 不存在于回收站");
        }
    }

    public static void clearUpRecovery(CommandSender sender,int day) {
        long mintime = System.currentTimeMillis() - (day * 86400000L);
        File dir = new File(Setting.RecoveryDir,"MMOCore");
        if (dir.isDirectory()){
            int i = 0;
            for (File file : Objects.requireNonNull(dir.listFiles())) {
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
            sender.sendMessage(" §b共清理了§f" + i + "§b个回收站数据");
        }
    }

    //清理没有插件数据的玩家数据
    public static void clearWorldPlayerData(CommandSender sender) {
        File dataDir;
        dataDir = new File("world/playerdata");
        clearupPlayerData(sender,dataDir);

        dataDir = new File("world/advancements");
        clearupPlayerData(sender,dataDir);

        dataDir = new File("world/stats");
        clearupPlayerData(sender,dataDir);
    }

    public static void clearOldFile(CommandSender sender) {
        File dir = new File("world/playerdata");
        if (dir.isDirectory()){
            //noinspection ConstantConditions
            for (File file : dir.listFiles()) {
                final String name = file.getName();
                if (name.endsWith(".dat_old")){
                    String source = name.substring(0,name.length() - 4);
                    final File sFile = new File(dir,source);
                    if (!sFile.exists())
                        sender.sendMessage(" §b删除文件:§f " + file.getName() + (file.delete() ? DONE : FAIL));
                }
            }
        }
    }

    //检查并删除目录
    public static void clearupPlayerData(CommandSender sender,File dataDir) {
        if (dataDir.isDirectory()){
            for (File file : Objects.requireNonNull(dataDir.listFiles())) {
                String name = file.getName();
                int w = name.lastIndexOf('.');
                if (w != -1){
                    name = name.substring(0,w);
                }
                File dc = new File(Setting.DataDir,name + ".yml");
                if (!dc.exists()){
                    sender.sendMessage(" §b删除文件:§f " + file.getName() + (file.delete() ? DONE : FAIL));
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteRecovery(CommandSender sender,String name) {
        DataConClearRecovervEvent event = new DataConClearRecovervEvent(sender,name);
        event.call();
        if (event.isCancelled()) return;
        File file = new File(Setting.RecoveryDir,name + ".zip");
        if (file.exists()){
            sender.sendMessage(" " + (file.delete() ? "已删除" : "无法删除") + "回收站: " + name);
        } else {
            sender.sendMessage(" 回收站不存在: " + name);
        }
    }

    //让回收站内数量保持在配置允许的数量
    public static void clearRecovery() {
        if (Setting.RecoveryDir.exists()){
            final String[] list = Setting.RecoveryDir.list();
            if (list == null) return;
            if (list.length > Setting.MaxRecovery){
                int num = list.length - Setting.MaxRecovery;
                File[] fileList = new File[list.length];
                for (int i = 0; i < list.length; i++) {
                    fileList[i] = new File(Setting.RecoveryDir,list[i]);
                }

                //收集距离修改时间最久的文件
                File[] clear = new File[num];
                long[] time = new long[num];
                for (File file : fileList) {
                    final long modified = file.lastModified();
                    for (int i = 0; i < clear.length; i++) {
                        if (clear[i] == null){
                            clear[i] = file;
                            time[i] = modified;
                            break;
                        } else if (time[i] > modified){
                            clear[i] = file;
                            time[i] = modified;
                            break;
                        }
                    }
                }

                //清理文件
                for (File file : clear) {
                    if (file != null){
                        file.delete();
                    }
                }

            }
        }
    }

    public static String[] canRecoverys() {
        if (Setting.RecoveryDir.exists()){
            String[] files = Setting.RecoveryDir.list();
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
        File recoveryDir = Setting.RecoveryDir;
        if (recoveryDir.exists()){
            File file = new File(recoveryDir,name + ".yml");
            return file.exists();
        }
        return false;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored","SynchronizationOnLocalVariableOrMethodParameter"})
    public static void delete(CommandSender commandSender,DataCon dc) {
        final UUID uuid = dc.getUUID();
        Runnable runnable = () -> {
            CommandSender sender = commandSender == null ? Bukkit.getConsoleSender() : commandSender;
            DataConDeleteEvent e = new DataConDeleteEvent(dc,commandSender);
            e.call();
            if (e.isCancelled()) return;
            Map<UUID, DataCon> playerDataMap = MMOCore.getPlayerDataMap();
            synchronized (playerDataMap) {
                MMOCore.getPlayerDataMap().remove(uuid);
                dc.save();
                dc.unload();
            }

            File playerdata = new File("world/playerdata",uuid.toString() + ".dat");
            File mmocore = new File(Setting.DataDir,uuid + ".yml");
            File advancements = new File("world/advancements",uuid.toString() + ".json");
            File stats = new File("world/stats",uuid.toString() + ".json");

            File zipFile = new File(Setting.RecoveryDir,dc.getName() + ".zip");
            if (!zipFile.exists()){
                try{
                    if (!Setting.RecoveryDir.exists()) Setting.RecoveryDir.mkdirs(); //创建回收站文件夹
                    if (zipFile.createNewFile()){
                        //成功创建文件,开始压缩
                        try (OutputStream outputStream = new FileOutputStream(zipFile); ZipOutputStream out = new ZipOutputStream(outputStream)){
                            zipWrite(out,playerdata,"playerdata");
                            zipWrite(out,mmocore,"mmocore");
                            zipWrite(out,advancements,"advancements");
                            zipWrite(out,stats,"stats");
                            sender.sendMessage("已备份当前数据到回收站");
                        }
                    }
                }catch (IOException ex){
                    sender.sendMessage(" §c无法备份到回收站: §r" + ex.getMessage());
                }
            } else {
                sender.sendMessage("回收站数据已存在，不备份当前数据");
            }

            File file = new File("world/playerdata",uuid.toString() + ".dat_old");
            if (file.exists()) file.delete(); //删除旧文件
            if (mmocore.exists()){
                sender.sendMessage("删除玩家数据" + (mmocore.delete() ? DONE : FAIL));
            }

            if (playerdata.exists()){
                sender.sendMessage("删除玩家存档" + (playerdata.delete() ? DONE : FAIL));
            }


            if (advancements.exists()){
                sender.sendMessage("删除玩家进度" + (advancements.delete() ? DONE : FAIL));
            }

            if (stats.exists()){
                sender.sendMessage("删除玩家状态" + (stats.delete() ? DONE : FAIL));
            }

            clearRecovery();
        };
        OfflineToPerform.execute(runnable,dc,commandSender);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void rename(CommandSender sender,DataCon dc,String newName) {
        if (!CommonUtils.checkName(newName)){
            sender.sendMessage("无效名称");
            return;
        }

        if (dc == null){
            sender.sendMessage("找不到玩家");
            return;
        }
        final String name = dc.getName();
        if (name.equals(newName)){
            sender.sendMessage("你想原地起飞吗?");
            return;
        }
        if (MMOCore.hasPlayerData(newName)){
            sender.sendMessage("ID已存在");
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
                    newDc.set("NameOnceUsed",name); //设置曾用名
                    newDc.set(DataCon.JOIN_KEY,dc.get(DataCon.JOIN_KEY)); //设置加入时间
                    newDc.save();
                    UUID uuid = dc.getUUID();
                    UUID newuuid = newDc.getUUID();

                    MMOCore.unLoad(uuid);

                    File dir = new File("world/playerdata");
                    File file = new File(dir,uuid.toString() + ".dat");
                    File newFile = new File(dir,newuuid.toString() + ".dat");
                    if (file.exists()){
                        if (newFile.exists()) newFile.delete();
                        sender.sendMessage("移动玩家数据" + (file.renameTo(newFile) ? DONE : FAIL));
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
                        if (newFile.exists()) newFile.delete();
                        sender.sendMessage("移动玩家进度" + (file.renameTo(newFile) ? DONE : FAIL));
                    }

                    dir = new File("world/stats");
                    file = new File(dir,uuid.toString() + ".json");
                    newFile = new File(dir,newuuid.toString() + ".json");
                    if (file.exists()){
                        if (newFile.exists()) newFile.delete();
                        sender.sendMessage("移动玩家状态" + (file.renameTo(newFile) ? DONE : FAIL));
                    }
                    sender.sendMessage("重命名完成");
                }

            }catch (Exception e){
                sender.sendMessage("重命名过程中出现异常" + e.getMessage());
                e.printStackTrace();
            }
        };

        OfflineToPerform.execute(runnable,dc,sender);
    }

    //加载列表
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

    //储存列表
    public static void saveList(File file,List<String> list) {
        try{
            if (!file.exists()){
                File p = file.getParentFile();
                if (!p.exists()){
                    p.mkdirs();
                }
                file.createNewFile();
            }
            Iterator<String> i = list.iterator();
            //1、打开流
            Writer w = new FileWriter(file);
            //2、写入内容
            while (i.hasNext()) {
                w.write(i.next());
                if (i.hasNext()) w.write('\n');
            }
            //3、关闭流
            w.close();
        }catch (IOException e){
            System.out.println("文件写入错误：" + e.getMessage());
        }
    }

    public static boolean zipWrite(ZipOutputStream output,File file,String fileName) {
        if (file.exists()){
            ZipEntry entry = new ZipEntry(fileName);
            try{
                output.putNextEntry(entry);
                try (FileInputStream input = new FileInputStream(file)){
                    byte[] buffer = new byte[2048];
                    int l;
                    while ((l = input.read(buffer)) != -1) {
                        output.write(buffer,0,l);
                    }
                }
                return true;
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static boolean zipRead(ZipInputStream input,File file) {
        final File parentFile = file.getParentFile();
        if (!parentFile.exists()) parentFile.mkdirs();
        if (!file.exists()){
            try{
                if (!file.createNewFile()){
                    return false;
                }
            }catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }
        try (FileOutputStream output = new FileOutputStream(file)){
            byte[] buff = new byte[2048];
            int l;
            while ((l = input.read(buff)) != -1) {
                output.write(buff,0,l);
            }
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }


    public static class OfflineToPerform implements Listener {
        public final String name;

        public OfflineToPerform(String name) {
            this.name = name;
        }

        public static void execute(Runnable runnable,DataCon dataCon,CommandSender sender) {
            if (!Bukkit.isPrimaryThread()){
                Bukkit.getScheduler().runTask(MMOCore.plugin,() -> execute(runnable,dataCon,sender));
                sender.sendMessage("切换到主线程执行");
            }
            var p = dataCon.getPlayer();
            if (p != null && p.isOnline()){
                var listener = new OfflineToPerform(dataCon.getName());
                Bukkit.getPluginManager().registerEvents(listener,MMOCore.plugin);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        var player = Bukkit.getPlayer(dataCon.getUUID());
                        if (player != null && player.isOnline()){
                            player.kickPlayer("阁下已被请出服务器");
                            sender.sendMessage("玩家当前在线，已kick玩家" + player.getDisplayName());
                        } else {
                            runnable.run();
                            sender.sendMessage("执行完毕");
                            cancel();
                        }
                    }

                    @Override
                    public synchronized void cancel() throws IllegalStateException {
                        super.cancel();
//                        HandlerList.unregisterAll(listener); //没必要用这个
                        AsyncPlayerPreLoginEvent.getHandlerList().unregister(listener);
                    }
                }.runTaskTimer(MMOCore.plugin,0L,2L);
            } else {
                runnable.run();
            }
        }

        @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
        public void onLogin(AsyncPlayerPreLoginEvent event) {
            if (!event.getName().equals(name)) return;
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
        }
    }

}
