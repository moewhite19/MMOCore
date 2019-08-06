package cn.whiteg.mmocore.listener;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.util.Frequent;
import cn.whiteg.mmocore.util.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class SafeNumEven implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (Frequent.CheckFrquent(event.getPlayer().getName(),10)){
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> PluginUtil.kickPlayer(event.getPlayer(),"§b阁下操作过于频繁"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandSendEvent event) {
        if (Frequent.CheckFrquent(event.getPlayer().getName(),10)){
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> PluginUtil.kickPlayer(event.getPlayer(),"§b阁下操作过于频繁"));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDed(PlayerDeathEvent event) {
        Player p = event.getEntity();
        if(Frequent.CheckFrquent(event.getEntity().getName(),300)){
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> PluginUtil.kickPlayer(event.getEntity(),"§b阁下操作过于频繁"));
        }
        //YamlUtils.setLocation(MMOCore.getPlayerData(p).getConfig(),"Player.Back",p.getLocation());
    }

//    @EventHandler
//    public void click(PlayerInteractEvent event) {
//        if(Frequent.CheckFrquent(event.getPlayer().getName(),10)){
//            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> PluginUtil.kickPlayer(event.getPlayer(),"§b阁下操作过于频繁"));
//        }
//        //YamlUtils.setLocation(MMOCore.getPlayerData(p).getConfig(),"Player.Back",p.getLocation());
//    }
    public void unreg() {
        PlayerDeathEvent.getHandlerList().unregister(this);
        AsyncPlayerChatEvent.getHandlerList().unregister(this);
        PlayerCommandSendEvent.getHandlerList().unregister(this);
    }
}
