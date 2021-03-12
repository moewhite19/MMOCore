package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class playsound extends HasCommandInterface {

    @Override
    public boolean executor(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            if (sender instanceof Player){
                Sound sound = Sound.parseYml(Setting.config.get(args[0]));
                if (sound.isEmpty()){
                    sender.sendMessage("无效音效");
                    return false;
                }
                sound.playTo((Player) sender);
                sender.sendMessage("播放成功");
            }
        } else if (args.length == 2){
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null){
                sender.sendMessage("找不到玩家");
                return false;
            }
            Sound sound = Sound.parseYml(Setting.config.get(args[0]));
            if (sound.isEmpty()){
                sender.sendMessage("无效音效");
                return false;
            }
            sound.playTo((Player) sender);
            sender.sendMessage("播放成功");
        }
        return true;
    }

    public List<String> completer(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 2){
            return getMatches(new ArrayList<String>(Setting.config.getKeys(false)),args);
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }
}
