package cn.whiteg.mmocore.listener;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class WorldSaveListener implements Listener {

    @EventHandler
    public void onSave(WorldSaveEvent event) {
        if (event.getWorld().getName().equals("world")){
            FileMan.SchedulSaveAll();
            if (Setting.DEBUG) MMOCore.logger.info("储存世界事件1");
        }
    }

        public void unreg() {
        MMOCore.logger.info("已注销事件");
        WorldSaveEvent.getHandlerList().unregister(this);
    }
}
