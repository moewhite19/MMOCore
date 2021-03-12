package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class reload extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        MMOCore.plugin.onReload();
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }
}
