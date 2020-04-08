package cn.whiteg.mmocore.Event;

import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DataConClearRecovervEvent extends Event implements Cancellable {
    private static HandlerList handler = new HandlerList();
    private final CommandSender sender;
    private final String name;
    private boolean cancelled = false;

    public DataConClearRecovervEvent(CommandSender sender, String name) {
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

    public String getName() {
        return name;
    }
}
