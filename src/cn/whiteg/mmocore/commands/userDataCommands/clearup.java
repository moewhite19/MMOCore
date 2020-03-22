package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class clearup extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 2){
            int day;
            try{
                day = Integer.valueOf(args[1]);
            }catch (NumberFormatException e){
                sender.sendMessage("参数有误");
                return false;
            }
            sender.sendMessage("开始清理" + day + "天前的回收站");
            FileMan.clearUpRecovery(sender,day);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            if (args.length == 2){
                return getMatches(Collections.singletonList("30"),args);
            }
        }
        return null;
    }
}
