package cn.whiteg.mmocore.Event;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DataConDeleteEvent extends Event implements Cancellable {
    private static HandlerList handler = new HandlerList();
    final private DataCon dataCon;
    private final CommandSender sender;
    private boolean cancelled = false;

    public DataConDeleteEvent(DataCon dataCon,CommandSender sender) {
        this.dataCon = dataCon;
        this.sender = sender;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    public DataCon getDataCon() {
        return dataCon;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }

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
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public CommandSender getSender() {
        return sender;
    }
}
