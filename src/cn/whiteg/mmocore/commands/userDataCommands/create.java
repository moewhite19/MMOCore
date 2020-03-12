package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class create extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return false;
        }
        if (args.length == 2){
            final DataCon dc = MMOCore.craftData(args[1]);
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
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
