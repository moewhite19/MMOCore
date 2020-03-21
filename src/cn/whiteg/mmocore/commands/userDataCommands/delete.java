package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class delete extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 2){
            String name = args[1];
            final DataCon dc = MMOCore.getPlayerData(name);
            if (dc != null){
                FileMan.delete(sender,dc);
            } else if (FileMan.canRecovery(name)){
                FileMan.deleteRecovery(sender,name);
            } else {
                sender.sendMessage("没有找到玩家");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
