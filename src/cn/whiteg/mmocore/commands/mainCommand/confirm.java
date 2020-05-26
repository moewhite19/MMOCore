package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.api.ReqestManage;
import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.container.ReqestContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class confirm extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender instanceof Player){
            boolean r = false;
            if (args.length == 1){
                r = ReqestManage.accept(sender,sender.getName());
            } else if (args.length == 2){
                r = ReqestManage.accept(sender,sender.getName(),args[1]);
            }
            if (!r){
                sender.sendMessage("§b没有待确认事件");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender instanceof Player){
            if (args.length == 2){
                ReqestContainer ce = ReqestManage.confirmMap.get(sender.getName());
                if (ce == null) return null;
                return getMatches(args[1],new ArrayList<>(ce.getKeys()));
            }
        }
        return null;
    }
}
