package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class save extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 1){
            if (sender instanceof Player){
                MMOCore.getPlayerData((MMOCore.getUUID(sender.getName()))).save();
                sender.sendMessage("储存玩家数据");
            } else sender.sendMessage("该指令只能玩家使用");
            return true;
        } else if (args.length == 2){
            String a = args[1];
            if (a.equals("@a") || a.equals("*")){
                FileMan.onSaveALL();
            } else {
                DataCon dc = MMOCore.getPlayerData(MMOCore.getUUID(a));
                if (dc != null){
                    dc.save();
                    sender.sendMessage("储存玩家数据");
                } else sender.sendMessage("没有找到玩家");
            }
        } else {
            sender.sendMessage("参数有误");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
