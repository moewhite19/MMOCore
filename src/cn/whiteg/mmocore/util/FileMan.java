package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.Event.DataConRenameEvent;
import cn.whiteg.mmocore.Event.DeleteDataConEvent;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import cn.whiteg.moeLogin.utils.PasswordUtils;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
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
/*            if(Setting.DEBUG) MMOCore.logger.info("异步线程加载");
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
                MMOCore.plugin.PlayerDataMap.put(player.getUniqueId().toString(),new DataCon(player));

            if(Setting.DEBUG) MMOCore.logger.info("线程已创建");
        */
        if (MMOCore.plugin.PlayerDataMap.containsKey(player.getName())) return;
        DataCon dc = new DataCon(player);
        MMOCore.plugin.PlayerDataMap.put(player.getUniqueId(),dc);
    }

    public static void SchedulSaveAll() {
        Bukkit.getScheduler().runTaskAsynchronously(MMOCore.plugin,() -> {
            final Iterator<Map.Entry<UUID, DataCon>> it = MMOCore.plugin.PlayerDataMap.entrySet().iterator();
            while (it.hasNext()) {
                final DataCon dc = it.next().getValue();
                if (dc == null){
                    it.remove();
                    continue;
                }
                Set<String> keys = dc.getConfig().getKeys(false);
                if (keys.size() == 0){
                    if (Setting.DEBUG){
                        MMOCore.logger.info("清理空配置" + dc.getName());
                    }
                    it.remove();
                    continue;
                }
//                if (pf.change){
//                    if (Setting.DEBUG) MMOCore.logger.info("储存" + entry.getKey());
//                    pf.Save();
//                }
                if (dc.isChange() && !dc.isNewFile) dc.checkSave();
/*
                if (Bukkit.getOnlinePlayers().size() != MMOCore.plugin.PlayerDataMap.size()){
                    Player player = Bukkit.getPlayer(entry.getKey().toString());
                    if (player == null || !player.isOnline()){
                        MMOCore.plugin.PlayerDataMap.delete(entry.getKey().toString());
                    }
                }
*/
                Player player = dc.getPlayer();
                if (player == null || !player.isOnline()){
                    if (Setting.DEBUG){
                        MMOCore.logger.info("清理过时离线玩家" + dc.getName());
                    }
                    it.remove();
                    dc.unload();
                }
            }
        });

    }

    public static void onSaveALL() {
        final Iterator<Map.Entry<UUID, DataCon>> it = MMOCore.plugin.PlayerDataMap.entrySet().iterator();
        while (it.hasNext()) {
            final DataCon dc = it.next().getValue();
            if (dc == null){
                it.remove();
                continue;
            }
            Set<String> keys = dc.getConfig().getKeys(false);
            if (keys.size() == 0){
                if (Setting.DEBUG){
                    MMOCore.logger.info("清理空配置" + dc.getName());
                }
                it.remove();
                continue;
            }
//                if (pf.change){
//                    if (Setting.DEBUG) MMOCore.logger.info("储存" + entry.getKey());
//                    pf.Save();
//                }
            if (dc.isChange() && !dc.isNewFile) dc.checkSave();
            Player player = dc.getPlayer();
            if (player == null || !player.isOnline()){
                if (Setting.DEBUG){
                    MMOCore.logger.info("清理过时离线玩家" + dc.getName());
                }
                it.remove();
                dc.unload();
            }
        }
        for (Map.Entry entry : MMOCore.plugin.PlayerDataMap.entrySet()) {
            DataCon pf = (DataCon) entry.getValue();
            if (pf == null) continue;
            pf.checkSave();
        }
    }

    public static void recovery(CommandSender sender,String name) {
        UUID uuid = MMOCore.getUUID(name);
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery");
        File file = new File(recoveryDir + File.separator + "playerdata",name + ".dat");
        File nFile = new File("world/playerdata",uuid.toString() + ".dat");
        File nDir = nFile.getParentFile();
        if (file.exists()){
            if (!nDir.exists()){
                nDir.mkdirs();
            }
            file.renameTo(nFile);
            sender.sendMessage("已恢复存档");
        }
        file = new File(recoveryDir + File.separator + "MMOCore",name + ".yml");
        nFile = new File("plugins/MMOCore/Player",uuid.toString() + ".yml");
        nDir = nFile.getParentFile();
        if (file.exists()){
            if (!nDir.exists()){
                nDir.mkdirs();
            }
            file.renameTo(nFile);
            sender.sendMessage("已恢复玩家数据");
        }

    }

    public static String[] canRecovery() {
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


    public static void delete(CommandSender sender,DataCon dc) {
        DeleteDataConEvent e = new DeleteDataConEvent(dc);
        e.call();
        if (e.isCancelled()) return;
        final Player p = Bukkit.getPlayerExact(dc.getName());
        if (p != null) p.kickPlayer("你被请出服务器");
        if (sender == null) sender = Bukkit.getConsoleSender();
        final UUID uuid = dc.getUUID();
        //if (uuid == null || uuid.isEmpty()) uuid = MMOCore.getOfflineUUID(args[1]).toString();
        dc.isNewFile = true;
        MMOCore.unLoad(dc.getUUID());
        File recoveryDir = new File(MMOCore.plugin.getDataFolder() + File.separator + "recovery");
        File file = new File("world/playerdata",uuid.toString() + ".dat");
        File nFile = new File(recoveryDir + File.separator + "playerdata",dc.getName() + ".dat");
        File nDir = nFile.getParentFile();
//        Logger logger = MMOCore.logger;
        if (file.exists()){
            if (!nDir.exists()) nDir.mkdirs();
            file.renameTo(nFile);
            sender.sendMessage("已删除玩家存档");
        }
        file = new File("plugins/MMOCore/Player",uuid.toString() + ".yml");
        nFile = new File(recoveryDir + File.separator + "MMOCore",dc.getName() + ".yml");
        nDir = nFile.getParentFile();
        if (file.exists()){
            if (!nDir.exists()) nDir.mkdirs();
            file.renameTo(nFile);
            sender.sendMessage("已删除玩家数据");
        }
        file = new File("world/advancements",uuid.toString() + ".json");
        if (file.exists()){
            file.delete();
            sender.sendMessage("已删除玩家进度");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Residence")){
            ResidenceManager rm = Residence.getInstance().getResidenceManager();
            rm.removeAllByOwner(dc.getName());
            sender.sendMessage("已删除玩家领地");
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
                DataConRenameEvent event = new DataConRenameEvent(dc,newId);
                event.call();
                if (event.isCancelled()) return;
                Player p = dc.getPlayer();
                if (p != null) p.kickPlayer("正在帮你重命名");
                MMOCore.plugin.PlayerDataMap.remove(dc.getUUID());
                DataCon newDc = MMOCore.craftData(newId);
                sender.sendMessage("创建新插件");
                for (String key : dc.getConfig().getKeys(true)) {
                    if (newDc.isSet(key)) continue;
                    newDc.set(key,dc.get(key));
                }
                UUID uuid = dc.getUUID();
                UUID newuuid = newDc.getUUID();
                //if (uuid == null || uuid.isEmpty()) uuid = MMOCore.getOfflineUUID(args[1]).toString();

                File file = new File("world/playerdata",uuid.toString() + ".dat");
                File newFile = new File("world/playerdata",newuuid.toString() + ".dat");
                if (file.exists()){
                    file.renameTo(newFile);
                    sender.sendMessage("以移动玩家数据");
                }
                file = new File("plugins/MMOCore/Player",uuid.toString() + ".yml");
                if (file.exists()){
                    file.delete();
                    sender.sendMessage("删除旧插件数据");
                }
                file = new File("world/advancements",uuid.toString() + ".json");
                newFile = new File("world/advancements",newuuid.toString() + ".json");
                if (file.exists()){
                    file.renameTo(newFile);
                    sender.sendMessage("已转移进度");
                }
                if (Bukkit.getPluginManager().isPluginEnabled("Residence")){
                    ResidenceManager rm = Residence.getInstance().getResidenceManager();
                    for (Map.Entry<String, ClaimedResidence> entry : rm.getResidences().entrySet()) {
                        ClaimedResidence res = entry.getValue();
                        if (res.getOwner().equals(dc.getName())){
                            res.getPermissions().setOwner(newId,true);
                            sender.sendMessage("已转移领地 " + res.getName());
                        }
                    }
                }
                newDc.isNewFile = false;
                sender.sendMessage("重命名完成");
            }catch (Exception e){
                sender.sendMessage("重命名过程中出现异常");
                e.printStackTrace();
            }
        });
    }
}
