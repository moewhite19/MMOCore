package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.MainCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class list extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 3){
            if (!(sender instanceof Player)){
                sender.sendMessage("该指令只能玩家使用");
                return true;
            }
            if (args[1].toLowerCase().equals("get"))
                sender.sendMessage("查询列表:" + MMOCore.getPlayerData(sender.getName()).getString(args[2]));
            return true;
        } else if (args.length == 4){
            if (args[1].toLowerCase().equals("set")){
                //   MMOCore.plugin.PlayerDataMap.get(((Player) sender).getUniqueId().toString()).set(args[2],args[3]);
                sender.sendMessage("设置字符串 " + args[1] + "为 " + args[2]);
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player != null)
                sender.sendMessage("查询字符串:" + MMOCore.getPlayerData(sender.getName()).getString(args[3]));
            else sender.sendMessage("没有找到玩家");
        } else {
            sender.sendMessage("参数有误");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            if (args.length == 2){
                return MainCommand.getMatches(args,MMOCore.getLoadDataNames());
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
