package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class save extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0){
            if (sender instanceof Player){
                MMOCore.getPlayerData((MMOCore.getUUID(sender.getName()))).save();
                sender.sendMessage("储存玩家数据");
            } else sender.sendMessage("参数有误");
            return true;
        } else if (args.length == 1){
            String a = args[0];
            if (a.equals("@a") || a.equals("*")){
                FileMan.onSaveALL();
            } else {
                DataCon dc = MMOCore.getPlayerData(MMOCore.getUUID(a));
                if (dc != null){
                    dc.save();
                    sender.sendMessage("储存玩家数据");
                } else sender.sendMessage("没有找到玩家");
            }
        } else {
            sender.sendMessage("参数有误");
        }
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "立即保存玩家数据";
    }
}
