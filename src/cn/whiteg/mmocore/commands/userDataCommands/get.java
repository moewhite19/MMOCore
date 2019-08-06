package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.MainCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class get extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 2){
            DataCon dc = MMOCore.getPlayerData(args[1]);
            if (dc != null){
                sender.sendMessage("包含值: ");
                sender.sendMessage(dc.getConfig().getKeys(false).toString());
            } else sender.sendMessage("没有找到玩家");
        } else if (args.length == 3){
            DataCon dc = MMOCore.getPlayerData(args[1]);
            if (dc != null){
                String v = dc.getString(args[2]);
                if (v == null){
                    sender.sendMessage("无字符串");
                } else {
                    sender.sendMessage("查询字符串: " + v.getClass().toGenericString());
                    sender.sendMessage(v);
                }
            } else sender.sendMessage("没有找到玩家");
        } else {
            sender.sendMessage("参数有误");
        }
        //(CraftPlayer)player.
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            if (args.length == 2){
                return MainCommand.getMatches(args[1],MMOCore.getLoadDataNames());
            }
            if (args.length == 3){
                DataCon dc = MMOCore.getPlayerData(args[1]);
                if (dc != null){
                    return MainCommand.getMatches(args[2],new ArrayList<String>(dc.getConfig().getKeys(true)));
                }
            }
        }
        return null;
    }
}
