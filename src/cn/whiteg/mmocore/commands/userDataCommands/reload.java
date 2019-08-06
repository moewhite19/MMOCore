package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class reload extends HasCommandInterface {
    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length < 1){
            sender.sendMessage("参数有误");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null){
            sender.sendMessage("玩家不存在");
            return true;
        }
        DataCon dc = new DataCon(player);

        MMOCore.getPlayerDataMap().put(player.getUniqueId(),dc);
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "重载玩家的插件数据:§7 <玩家id>";
    }
}
