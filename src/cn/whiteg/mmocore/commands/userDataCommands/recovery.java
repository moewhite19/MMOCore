package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class recovery extends CommandInterface {
    WeakReference<List<String>> cacheList = new WeakReference<>(null);

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length == 2){
            sender.sendMessage("开始尝试恢复玩家数据");
            FileMan.recovery(sender,args[1]);

        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            if (args.length == 2){
                List<String> list = cacheList.get();
                if (list == null){
                    list = Arrays.asList(FileMan.canRecoverys());
                    cacheList = new WeakReference<>(list);
                }
                return getMatches(list,args);
            }
        }
        return null;
    }
}
