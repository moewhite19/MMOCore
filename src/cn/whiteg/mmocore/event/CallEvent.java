package cn.whiteg.mmocore.event;

import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CallEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    public void call() {
        if (Bukkit.isPrimaryThread()){
            Bukkit.getPluginManager().callEvent(this);
        } else {
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
                Bukkit.getPluginManager().callEvent(this);
            });
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
