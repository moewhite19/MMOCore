package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.CommandManage;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class get extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            DataCon dc = MMOCore.getPlayerData(args[0]);
            if (dc != null){
                sender.sendMessage("包含值: ");
                sender.sendMessage(dc.getConfig().getKeys(false).toString());
            } else sender.sendMessage("没有找到玩家");
        } else if (args.length == 2){
            DataCon dc = MMOCore.getPlayerData(args[0]);
            if (dc != null){
                String v = dc.getString(args[1]);
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
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            return CommandManage.getMatches(args[0],MMOCore.getLoadDataNames());
        }
        if (args.length == 2){
            DataCon dc = MMOCore.getPlayerData(args[0]);
            if (dc != null){
                return CommandManage.getMatches(args[1],new ArrayList<String>(dc.getConfig().getKeys(true)));
            }
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "读取玩家的插件数据:§7 <玩家id> <数据节点>";
    }
}
