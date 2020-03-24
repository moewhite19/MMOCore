package cn.whiteg.mmocore.Event;

import cn.whiteg.mmocore.DataCon;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DataConRenameEvent extends Event implements Cancellable {
    private static HandlerList handler = new HandlerList();
    final private DataCon dataCon;
    private final String name;
    final private String newId;
    private final CommandSender sender;
    private boolean cancelled = false;

    public DataConRenameEvent(DataCon dataCon,String name,String newId,CommandSender sender) {
        this.dataCon = dataCon;
        this.name = name;
        this.newId = newId;
        this.sender = sender;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    public DataCon getDataCon() {
        return dataCon;
    }

    public String getName() {
        return name;
    }

    public String getNewName() {
        return newId;
    }

    public CommandSender getSender() {
        return sender;
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
