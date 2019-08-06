package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class rename extends HasCommandInterface {
    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 2){
            String name = args[0];
            DataCon dc = MMOCore.getPlayerData(name);
            String newName = args[1];
            FileMan.rename(sender,dc,newName);
            return true;
//            dc.set("Player.by",sender.getName());
        }
        sender.sendMessage("/ud rename <玩家ID> <新ID>");
        return false;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        return getMatches(args,MMOCore.getLatelyPlayerList());
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "为玩家重命名:§7 <旧id> <新id>";
    }
}
