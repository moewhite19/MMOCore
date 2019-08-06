package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class create extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return false;
        }
        if (args.length == 1){
            final DataCon dc = MMOCore.craftData(args[0]);
            dc.set("Player.by",sender.getName());
            if (dc.isLoaded()){
                sender.sendMessage("创建成功");
                return true;
            }
            sender.sendMessage("创建失败");
            return false;
        }
        sender.sendMessage("参数有误");
        return false;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "为玩家创建插件数据: §7<玩家ID>";
    }
}
