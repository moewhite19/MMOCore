package cn.whiteg.mmocore.event;

import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class DataConClearRecovervEvent extends CallEvent {
    private static final HandlerList handler = new HandlerList();
    private final CommandSender sender;
    private final String name;

    public DataConClearRecovervEvent(CommandSender sender,String name) {
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

    public void call() {
        if (Bukkit.isPrimaryThread()){
            Bukkit.getPluginManager().callEvent(this);
        } else {
            Bukkit.getScheduler().runTask(MMOCore.plugin,() -> {
                Bukkit.getPluginManager().callEvent(this);
            });
        }
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getName() {
        return name;
    }
}
