package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class rename extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 3){
            DataCon dc = MMOCore.getPlayerData(args[1]);
            String newId = args[2];
            FileMan.rename(sender,dc,newId);
            return true;
//            dc.set("Player.by",sender.getName());
        }
        sender.sendMessage("/ud rename <玩家ID> <新ID>");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
