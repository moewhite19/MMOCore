package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

public class recovery extends HasCommandInterface {
    WeakReference<List<String>> cacheList = new WeakReference<>(null);

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            sender.sendMessage("开始尝试恢复玩家数据");
            FileMan.recovery(sender,args[0]);

        }
        return false;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            List<String> list = cacheList.get();
            if (list == null){
                list = Arrays.asList(FileMan.canRecoverys());
                cacheList = new WeakReference<>(list);
            }
            return getMatches(list,args);
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "从回收站恢复玩家数据:§7 <玩家id>";
    }
}
