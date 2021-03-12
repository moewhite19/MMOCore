package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class test extends CommandInterface {

    final private MMOCore plugin;

    public test(MMOCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        sender.sendMessage("插件 " + plugin);
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }
}
