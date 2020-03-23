package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.common.CommandInterface;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class getuuid extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 2){
            UUID uuid = MMOCore.getUUID(args[1]);
            if (uuid == null){
                sender.sendMessage("没有找到玩家");
                return true;
            }
            String msg = args[1] + (Setting.onlineMode ? "§a在线模式" : "§b离线模式");
            if (sender instanceof Player){
                BaseComponent[] bse = new ComponentBuilder(msg)
                        .append(uuid.toString())
                        .color(ChatColor.WHITE)
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,uuid.toString()))
                        .create();
                sender.spigot().sendMessage(bse);
                return true;
            }
            sender.sendMessage(msg + uuid.toString());
        } else {
            sender.sendMessage("请加上要查询的玩家ID");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
