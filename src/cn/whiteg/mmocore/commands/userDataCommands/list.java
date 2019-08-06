package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.CommandManage;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class list extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length > 3){
            final String method = args[0];
            final String name = args[1];
            final String pat = args[2];
            final DataCon playerData = MMOCore.getPlayerData(name);
            if (playerData == null){
                sender.hasPermission(" §b找不到玩家§f" + name);
                return false;
            }
            String message = null;
            if (method.equalsIgnoreCase("get")){
                //   MMOCore.plugin.PlayerDataMap.get(((Player) sender).getUniqueId().toString()).set(args[2],args[3]);
                message = playerData.getString(pat);
            } else if (method.equalsIgnoreCase("set")){
                //todo 暂不支持set列表
                message = "暂时不支持set";
            }
            if (message != null){
                sender.sendMessage("设置字符串 " + method + "为 :" + message);
                return true;
            }
        }

        sender.sendMessage("参数有误");
        return false;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 2){
            return CommandManage.getMatches(args,MMOCore.getLoadDataNames());
        }
        if (args.length == 3){
            DataCon dc = MMOCore.getPlayerData(args[1]);
            if (dc != null){
                return CommandManage.getMatches(args[2],new ArrayList<String>(dc.getConfig().getKeys(true)));
            }
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "读取自己插件数据列表";
    }
}
