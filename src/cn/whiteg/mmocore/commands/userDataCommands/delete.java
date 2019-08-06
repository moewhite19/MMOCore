package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class delete extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            String name = args[0];
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
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        return getMatches(args,MMOCore.getLatelyPlayerList());
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "删除玩家数据:§7 <玩家ID>";
    }
}
