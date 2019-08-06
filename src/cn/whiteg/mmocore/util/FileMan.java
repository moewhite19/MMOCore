package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.Event.DeleteDataConEvent;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.listener.WorldSaveListener;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ResidenceManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

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
                if (dc.isChange() && !dc.isNewFile) dc.Save();
/*
                if (Bukkit.getOnlinePlayers().size() != MMOCore.plugin.PlayerDataMap.size()){
                    Player player = Bukkit.getPlayer(entry.getKey().toString());
                    if (player == null || !player.isOnline()){
                        MMOCore.plugin.PlayerDataMap.delete(entry.getKey().toString());
                    }
                }
*/
                if (dc.getPlayer() == null){
                    if (Setting.DEBUG){
                        MMOCore.logger.info("清理过时离线玩家" + dc.getName());
                    }
                    it.remove();
                    dc.unload();
                }
            }
            WorldSaveListener.savein = 0;
        });

    }

    public static void onSaveALL() {
        for (Map.Entry entry : MMOCore.plugin.PlayerDataMap.entrySet()) {
            DataCon pf = (DataCon) entry.getValue();
            if (pf == null) continue;
            pf.Save();
        }
    }

    public static void delete(DataCon dc) {
        DeleteDataConEvent e = new DeleteDataConEvent(dc);
        e.call();
        if (e.isCancelled()) return;
        final UUID uuid = dc.getUUID();
        //if (uuid == null || uuid.isEmpty()) uuid = MMOCore.getOfflineUUID(args[1]).toString();
        dc.isNewFile = true;
        MMOCore.unLoad(dc.getUUID());

        File file = new File("world/playerdata",uuid.toString() + ".dat");
        Logger logger = MMOCore.logger;
        if (file.exists()){
            file.delete();
            logger.info("已删除玩家存档");
        }
        file = new File("plugins/MMOCore/Player",uuid.toString() + ".yml");
        if (file.exists()){
            file.delete();
            logger.info("已删除玩家数据");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Residence")){
            ResidenceManager rm = Residence.getInstance().getResidenceManager();
            rm.removeAllByOwner(dc.getName());
            logger.info("已删除玩家领地");
        }
        final Player p = Bukkit.getPlayerExact(dc.getName());
        if (p != null) p.kickPlayer("你被请出服务器");

    }
}
