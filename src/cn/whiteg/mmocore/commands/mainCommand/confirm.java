package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.api.ReqestManage;
import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.container.ReqestContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class confirm extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        boolean r = false;
        if (args.length == 0){
            r = ReqestManage.accept(sender,sender.getName());
        } else if (args.length == 1){
            r = ReqestManage.accept(sender,sender.getName(),args[0]);
        }
        if (!r){
            sender.sendMessage("§b没有待确认事件");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            ReqestContainer ce = ReqestManage.confirmMap.get(sender.getName());
            if (ce == null) return null;
            return getMatches(args[0],new ArrayList<>(ce.getKeys()));
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "接收请求";
    }
}
