package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class load extends HasCommandInterface {

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
        FileMan.load(player);
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    @Override
    public String getDescription() {
        return "加载玩家的插件数据:§7 <玩家id>";
    }
}
