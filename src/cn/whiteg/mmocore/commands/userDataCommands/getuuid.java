package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.common.HasCommandInterface;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class getuuid extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            UUID uuid = MMOCore.getUUID(args[0]);
            if (uuid == null){
                sender.sendMessage("没有找到玩家");
                return true;
            }
            String msg = args[0] + (Setting.onlineMode ? "§a在线模式" : "§b离线模式");
            if (sender instanceof Player){
                BaseComponent[] bse = new ComponentBuilder(msg)
                        .append(uuid.toString())
                        .color(ChatColor.WHITE)
                        .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,uuid.toString()))
                        .create();
                sender.spigot().sendMessage(bse);
                return true;
            }
            sender.sendMessage(msg + uuid);
        } else {
            sender.sendMessage(getDescription());
        }
        return false;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public List<String> complete(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String str,@NotNull String[] args) {
        return getMatches(args,MMOCore.getLatelyPlayerList());
    }

    @Override
    public String getDescription() {
        return "获取玩家UUID: <玩家ID>";
    }
}
