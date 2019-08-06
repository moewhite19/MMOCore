package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.MainCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class set extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        if (args.length < 4){
            return true;
        }
        Object val = args[3];
        if (args.length == 4){
            DataCon dc = MMOCore.getPlayerData(args[1]);
            if (dc == null){
                sender.sendMessage("数据不存在");
                return true;
            }

            String str = (String) val;
            if (!str.startsWith("\"") && !str.endsWith("\"")){
                double d;
                try{
                    d = Double.valueOf(str);
                }catch (NumberFormatException e){
                    d = 0;
                }
                if (d != 0) val = d;
            }

            dc.set(args[2],val);
            sender.sendMessage("设置字符串 " + args[1] + "为 " + val);
        } else sender.sendMessage("参数有误");

        //(CraftPlayer)player.
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            if (args.length == 2){
                return MainCommand.getMatches(args[1],MMOCore.getLoadDataNames());
            }
            if (args.length == 3){
                DataCon dc = MMOCore.getPlayerData(args[1]);
                if (dc != null){
                    return MainCommand.getMatches(args[2],new ArrayList<String>(dc.getConfig().getKeys(true)));
                }
            }
        }
        return null;
    }
}
