package cn.whiteg.mmocore.event;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DataConRecovervEvent extends CallEvent implements Cancellable {
    private static final HandlerList handler = new HandlerList();
    private final CommandSender sender;
    private final String name;
    private DataCon dc = null;

    public DataConRecovervEvent(CommandSender sender,String name) {
        this.sender = sender;
        this.name = name;

    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getName() {
        return name;
    }

    public DataCon getDataCon() {
        if (dc == null) dc = MMOCore.getPlayerData(name);
        return dc;
    }

}
