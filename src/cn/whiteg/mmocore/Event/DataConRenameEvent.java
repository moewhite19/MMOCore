package cn.whiteg.mmocore.Event;

import cn.whiteg.mmocore.DataCon;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DataConRenameEvent extends Event implements Cancellable {
    private static HandlerList handler = new HandlerList();
    final private DataCon dataCon;
    final private String newId;
    private boolean cancelled = false;

    public DataConRenameEvent(DataCon dataCon,String newId) {
        this.dataCon = dataCon;
        this.newId = newId;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    public DataCon getDataCon() {
        return dataCon;
    }

    public String getNewId() {
        return newId;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public void call() {
        Bukkit.getPluginManager().callEvent(this);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
