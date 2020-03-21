package cn.whiteg.mmocore.listener;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyLogin(AsyncPlayerPreLoginEvent event) {
        final String name = event.getName();
        final Player np = Bukkit.getPlayer(name);
        if (np != null && np.getName().equalsIgnoreCase(name)){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,"服务器已存在相同ID");
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event){
        MMOCore.craftData(event.getPlayer());
    }
}
