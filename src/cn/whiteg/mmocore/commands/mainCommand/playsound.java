package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class playsound extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")) return false;
        if (args.length == 2){
            if (sender instanceof Player){
                Sound sound = Sound.parseYml(Setting.config.get(args[1]));
                if (sound.isEmpty()){
                    sender.sendMessage("无效音效");
                    return false;
                }
                sound.playTo((Player) sender);
                sender.sendMessage("播放成功");
            }
        } else if (args.length == 3){
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null){
                sender.sendMessage("找不到玩家");
                return false;
            }
            Sound sound = Sound.parseYml(Setting.config.get(args[1]));
            if (sound.isEmpty()){
                sender.sendMessage("无效音效");
                return false;
            }
            sound.playTo((Player) sender);
            sender.sendMessage("播放成功");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")) return null;
        if (args.length == 2){
            return getMatches(new ArrayList<String>(Setting.config.getKeys(false)),args);
        }
        return null;
    }
}
