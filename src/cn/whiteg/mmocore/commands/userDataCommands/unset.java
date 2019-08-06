package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.CommandManage;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class unset extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 2){
            DataCon dc = MMOCore.getPlayerData(args[0]);
            if (dc == null){
                sender.sendMessage("数据不存在");
                return true;
            }
            dc.set(args[1],null);
            sender.sendMessage("删除字符串 " + args[0]);
        } else sender.sendMessage("参数有误");
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
        return "删除玩家的插件数据节点:§7 <玩家id> <数据节点>";
    }
}
