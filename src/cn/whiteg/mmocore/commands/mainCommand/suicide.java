package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.api.ReqestManage;
import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.mmocore.container.Reqest;
import cn.whiteg.mmocore.container.ReqestAbs;
import cn.whiteg.mmocore.util.CoolDownUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class suicide extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender instanceof Player player){
//            final Reqest reqest = new ReqestAbs() {
//                @Override
//                public void onAccept(CommandSender s) {
//                    if (s instanceof Player p){
//                        p.chat("再见 这个世界");
//                        p.setHealth(0);
//                    }
//                }
//
//                @Override
//                public void onDeny(CommandSender s) {
//                    if (s instanceof Player p) p.chat("世界很大，我很喜欢这个世界");
//                }
//
//                @Override
//                public void onCanel() {
//                    if (player.isOnline()){
//                        player.chat("世界很大，我很喜欢这个世界,可是世界不喜欢我....");
//                    }
//                }
//            };
//            ReqestManage.setEvent(player.getName(),"suicide",reqest);
//            ComponentBuilder cb = new ComponentBuilder(" §b输入或者点击");
//            cb.append("§a/confirm suicide");
//            cb.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/confirm suicide"));
//            cb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text("§3点我或者使用指令§a/c§3确认，也可以使用指令§c/d§3取消")));
//            cb.append("§b来确认");
//            cb.reset();
//            BaseComponent[] msg = cb.create();
//            sender.spigot().sendMessage(msg);
            if (CoolDownUtil.hasCd(player.getName(),this.getName())){
                CoolDownUtil.setCd(player.getName(),getName(),30000);
                player.chat("再见 这个世界");
                player.setHealth(0);
                return true;
            } else {
                player.chat("世界很大，我很喜欢这个世界,可是世界不喜欢我....");
            }
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("mmo.suicide");
    }

    @Override
    public String getDescription() {
        return "离开这个世界";
    }
}
