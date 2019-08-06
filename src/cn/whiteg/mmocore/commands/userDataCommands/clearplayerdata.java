package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.util.FileMan;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;

public class clearplayerdata extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        sender.sendMessage("开始清理没有插件数据的玩家");
        FileMan.clearWorldPlayerData(sender); //清理没有插件数据的玩家数据

        //清理没有玩家数据的插件数据
        var dir = new File("world/playerdata");
        var it = MMOCore.iteratorPlayerData();
        while (it.hasNext()) {
            var dc = it.next();
            var uuid = dc.getUUID();
            var pd = new File(dir,uuid + ".dat");
            if (pd.exists()) continue;
            sender.sendMessage(" §b没有游戏数据:§f " + dc.getName());
            it.remove();
        }

        FileMan.clearOldFile(sender); //清理无效的_old文件
        return true;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "清理没有插件数据的玩家";
    }
}
