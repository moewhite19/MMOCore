package cn.whiteg.mmocore.listener;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.event.DataConDeleteEvent;
import cn.whiteg.mmocore.event.DataConRenameEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.LinkedList;

public class MMOCoreListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAsyLogin(AsyncPlayerPreLoginEvent event) {
        final String name = event.getName();
        final Player np = Bukkit.getPlayerExact(name);
        if (np != null && np.getName().equalsIgnoreCase(name)){
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,"服务器已存在相同ID");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
            DataCon dc = MMOCore.getPlayerData(event.getPlayer());
            if (dc == null) return;
            dc.setString("Player.quit_time",String.valueOf(System.currentTimeMillis()));
            dc.save();
            MMOCore.unLoad(dc.getUUID());
        });
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        DataCon dc = MMOCore.craftData(event.getPlayer());
        if (dc.isLoaded()) dc.update(event.getPlayer());

        //更新最近玩家列表
        String name = event.getPlayer().getName();
        LinkedList<String> list = (LinkedList<String>) MMOCore.getLatelyPlayerList();
        list.remove(name);
        list.add(0,name.intern());
        while (list.size() > Setting.LatelyPlayerListSize) {
            list.removeLast();
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void remove(DataConRenameEvent event) {
        //当玩家重命名时更新
        var list = MMOCore.getLatelyPlayerList();
        list.remove(event.getName());
        list.add(0,event.getNewName().intern());
    }

    @EventHandler(ignoreCancelled = true)
    public void delete(DataConDeleteEvent event) {
        //当删除玩家时从最近玩家列表移除
        var list = MMOCore.getLatelyPlayerList();
        list.remove(event.getDataCon().getName());
    }
}
