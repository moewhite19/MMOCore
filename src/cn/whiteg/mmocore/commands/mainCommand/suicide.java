package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.api.ReqestManage;
import cn.whiteg.mmocore.container.Reqest;
import cn.whiteg.mmocore.container.ReqestAbs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class suicide extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (sender.hasPermission("mmo.suicide")){
                final Reqest reqest = new ReqestAbs() {
                    @Override
                    public void onAccept() {
                        player.chat("再见 这个世界");
                        player.setHealth(0);
                    }

                    @Override
                    public void onDeny() {
                        player.sendMessage("世界很大，我很喜欢这个世界");
                    }

                    @Override
                    public void onCanel() {
                        player.sendMessage("世界很大，我很喜欢这个世界,可是世界不喜欢我....");
                    }
                };
                ReqestManage.setEvent(player.getName(),"suicide",reqest);
                sender.sendMessage("输入指令/config suicide确认");
            } else {
                sender.sendMessage("阁下没有权限");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
