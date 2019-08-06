package cn.whiteg.mmocore.event;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DataConDeleteEvent extends CallEvent {
    private static final HandlerList handler = new HandlerList();
    final private DataCon dataCon;
    private final CommandSender sender;

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

    public CommandSender getSender() {
        return sender;
    }
}
