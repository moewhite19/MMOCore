package cn.whiteg.mmocore.listener;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
            DataCon py = MMOCore.getPlayerData(event.getPlayer());
            if (py == null) return;
            py.setString("Player.quit_time",String.valueOf(System.currentTimeMillis()));
            py.save();
            MMOCore.unLoad(py.getUUID());
        });
    }

    public void unreg() {
        PlayerQuitEvent.getHandlerList().unregister(this);
    }
}
