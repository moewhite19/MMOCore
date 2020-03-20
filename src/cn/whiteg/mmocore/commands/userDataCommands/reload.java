package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class reload extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length < 2){
            sender.sendMessage("参数有误");
            return true;
        }
        Player player = Bukkit.getPlayer(args[1]);
        if (player == null){
            sender.sendMessage("玩家不存在");
            return true;
        }
        DataCon dc = new DataCon(player);

        MMOCore.getPlayerDataMap().put(player.getUniqueId(),dc);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
