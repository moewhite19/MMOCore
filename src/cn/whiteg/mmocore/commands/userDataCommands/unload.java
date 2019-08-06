package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class unload extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length < 1){
            sender.sendMessage("参数有误");
            return true;
        }
        UUID uuid = MMOCore.getUUID(args[0]);
        DataCon dc = MMOCore.unLoad(uuid);
        sender.sendMessage(dc == null ? "没有卸载" : "已卸载");
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "卸载玩家的插件数据:§7 <玩家id>";
    }
}
