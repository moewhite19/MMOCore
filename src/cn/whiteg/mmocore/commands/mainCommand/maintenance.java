package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;

public class maintenance extends HasCommandInterface implements Listener {
    boolean enable = false;

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        enable = !enable;
        if (enable) MMOCore.plugin.regListener(this);
        else MMOCore.plugin.unregListener(this);
        sender.sendMessage("§b已" + (enable ? "§a开启" : "§c关闭") + "§b维护模式");
        return true;
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        if (!enable) return;
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,"服务器当前正在维护");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "开关维护模式";
    }
}
