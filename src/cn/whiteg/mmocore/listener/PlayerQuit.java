package cn.whiteg.mmocore.listener;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
            DataCon py = MMOCore.getPlayerData(event.getPlayer());
            if (py == null) return;
            py.setString("Player.quit_time",String.valueOf(System.currentTimeMillis()));
            if (Setting.DELETE_CACHE){
                Bukkit.getScheduler().runTaskAsynchronously(MMOCore.plugin,() -> {
                    if (py.isNewFile){
                        File dm = new File("world/playerdata",py.getUUID().toString() + ".dat");
                        if (dm.exists()){
                            dm.delete();
                            MMOCore.logger.info("已删除玩家存档" + py.getName());
                        }
                    } else {
                        py.Save();
                    }
                });
                MMOCore.unLoad(py.getUUID());
            } else {
                py.Save();
            }
        });
    }

    public void unreg() {
        PlayerQuitEvent.getHandlerList().unregister(this);
    }
}
