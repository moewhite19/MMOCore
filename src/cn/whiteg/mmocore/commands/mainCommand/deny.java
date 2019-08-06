package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.api.ReqestManage;
import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.container.Reqest;
import cn.whiteg.mmocore.container.ReqestContainer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class deny extends CommandInterface {
    final static String msg_f = "§b没有待确认事件";

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender instanceof Player){
            final ReqestContainer cf = ReqestManage.getContainer(sender.getName());
            if (cf == null){
                sender.sendMessage(msg_f);
                return false;
            }
            if (args.length == 0){
                final Reqest reqest = cf.getLastReqest();
                if (reqest != null){
                    cf.getLastReqest().deny(sender);
                    return true;
                }
                sender.sendMessage(msg_f);
            } else if (args.length == 1){
                final Reqest reqest = cf.getRequest(args[0]);
                if (reqest != null){
                    reqest.deny(sender);
                    return true;
                }
                sender.sendMessage(msg_f);

            } else {
                sender.sendMessage("参数有误");
            }
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender instanceof Player){
            if (args.length == 1){
                ReqestContainer ce = ReqestManage.getContainer(sender.getName());
                if (ce == null) return null;
                return getMatches(args[0],new ArrayList<>(ce.getKeys()));
            }
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "拒绝请求";
    }
}
