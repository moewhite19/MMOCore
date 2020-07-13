package cn.whiteg.mmocore.container;

import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.api.ReqestManage;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class PlayerReqest extends ReqestAbs {
    final private Player sender;
    final private String name;
    final private String id;

    public PlayerReqest(final Player sender,final String name) {
        this.sender = sender;
        this.name = name;
        this.id = name + "@" + sender.getName();
    }

    @Override
    public abstract void onAccept(CommandSender sender);

    /**
     * 拒绝时调用
     */
    @Override
    public void onDeny(CommandSender s) {
        if (s instanceof Player){
            sender.sendMessage(" §f" + ((Player) s).getDisplayName() + "§r§b已拒绝阁下的§f" + name + "§b请求");
            s.sendMessage(" §b已拒绝§f" + name + "§b请求");
        }
    }

    @Override
    public void onCanel() {
    }

    public boolean sendTo(Player p) {
        if (p.isOnline() && ReqestManage.addEvent(p.getName(),id,this)){
            p.spigot().sendMessage(getBaseComponentMessage());
            sender.sendMessage(" §b已向 §f" + p.getDisplayName() + " §b发送" + name + "请求");
            playsound(p);
            return true;
        }
        sender.sendMessage(" " + p.getDisplayName() + "§b已存在一个相同请求");
        return false;
    }

    //发送给谁所有人
    public void sendAll() {
        int i = 0;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p == sender) continue;
            if (p.isOnline() && ReqestManage.addEvent(p.getName(),id,this)){
                p.spigot().sendMessage(getBaseComponentMessage());
                playsound(p);
                i++;
            } else {
                sender.sendMessage(" " + p.getDisplayName() + " §b已存在一个相同请求");
            }
        }
        sender.sendMessage(" §b已发送 §f" + i + " §b个" + name + "请求");
    }

    BaseComponent[] getBaseComponentMessage() {
        ComponentBuilder cb = new ComponentBuilder(" §f" + sender.getDisplayName() + " §7给你发送了一个 §f" + name + "§7 请求");
        cb.append(" §b§l[接受]");
        cb.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/confirm " + id));
        cb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[]{new TextComponent("§3点我或者使用指令§a/c " + id + "§a接受")}));
        cb.append(" §8§l[拒绝]");
        cb.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/deny " + id));
        cb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[]{new TextComponent("§3点我或者使用指令§a/d " + id + "§4拒绝")}));
        return cb.create();
    }

    public Player getSender() {
        return sender;
    }

    @Override
    public void accept(CommandSender s) {
        remove(s);
        if (sender.isOnline()){
            onAccept(s);
        } else {
            s.sendMessage("§b对方已下线");
        }
    }

    @Override
    public void deny(CommandSender s) {
        remove(s);
        if (sender.isOnline()){
            onDeny(s);
        } else {
            s.sendMessage("§b对方已下线");
        }
    }


    public void playsound(Player p) {
        Setting.PlayerReqestSound.clone().playTo(p);
    }

    public void remove(CommandSender s) {
        ReqestContainer c = ReqestManage.getContainer(s.getName(),false);
        if (c == null) return;
        c.remove(this);
    }
}
