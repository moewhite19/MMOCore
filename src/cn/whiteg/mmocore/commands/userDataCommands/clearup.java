package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class clearup extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 1){
            int day;
            try{
                day = Integer.parseInt(args[0]);
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
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            if (args.length == 1){
                return getMatches(Collections.singletonList("30"),args);
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
        return "清理回收站:§7<[多少天前>";
    }
}
