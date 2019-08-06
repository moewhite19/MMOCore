package cn.whiteg.mmocore.event;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DataConRenameEvent extends CallEvent implements Cancellable {
    private static final HandlerList handler = new HandlerList();
    final private DataCon dataCon;
    private final String name;
    final private String newId;
    private final CommandSender sender;

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
}
