package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.MMOCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
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
            if (sender instanceof Player){
                BaseComponent[] bse = new ComponentBuilder(Bukkit.getServer().getOnlineMode() ? "§a在线模式" : "§b离线模式")
                        .append(uuid.toString())
                        .color(ChatColor.WHITE)
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,uuid.toString()))
                        .create();
                sender.spigot().sendMessage(bse);
                return true;
            }
            sender.sendMessage(Bukkit.getServer().getOnlineMode() ? "§a在线模式" : "§b离线模式" + "§f " + uuid);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
