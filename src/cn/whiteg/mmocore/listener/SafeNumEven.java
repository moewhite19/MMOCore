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
import org.bukkit.event.player.PlayerTeleportEvent;

public class SafeNumEven implements Listener {
    final static String msg = "§b阁下操作过于频繁";

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (Frequent.CheckFrquent(event.getPlayer().getName(),200)){
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
//                PluginUtil.kickPlayer(event.getPlayer(),"§b阁下操作过于频繁");
//                event.setCancelled(true);
                event.getPlayer().sendMessage(msg);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"mute " + event.getPlayer().getName() + " 10m");
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandSendEvent event) {
        final Player p = event.getPlayer();
        if (Frequent.CheckFrquent(p.getName(),100)){
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
//                PluginUtil.kickPlayer(event.getPlayer(),"");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"ml ban " + p.getName() + " 10m " + msg);
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDed(PlayerDeathEvent event) {
        final Player p = event.getEntity();
        if (Frequent.CheckFrquent(p.getName(),300)){
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> PluginUtil.kickPlayer(p,msg));
        }
    }

//    @EventHandler(ignoreCancelled = true)
//    public void buildBlock(BlockCanBuildEvent event) {
//
//    }


    @EventHandler(ignoreCancelled = true)
    public void onTp(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_GATEWAY || event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
            return;
//        Location from = event.getFrom();
//        Location tp = event.getTo();
        final Player p = event.getPlayer();
        if (Frequent.CheckFrquent(p.getName(),350)){
            event.setCancelled(true);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"ml ban " + event.getPlayer().getName() + " 10m " + msg);
        }
    }

/*

    //
    @EventHandler
    public void click(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            ItemStack item = event.getItem();
            if (item != null && item.getType().isAir()) return;
        }
        if (Frequent.CheckFrquent(event.getPlayer().getName(),10)){
            event.setCancelled(true);
            Bukkit.getScheduler().accept(MMOCore.plugin,() -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"ml ban " + event.getPlayer().getName() + " 5m " + msg);
            });
        }
    }
*/


}
