package cn.whiteg.mmocore.container;

import cn.whiteg.mmocore.api.ReqestManage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public abstract class PlayerReqest extends ReqestAbs {
    final private Player player;
    final private Player sender;
    final private String name;
    final private String id;

    public PlayerReqest(final Player player,final Player sender,final String name) {
        this.player = player;
        this.sender = sender;
        this.name = name;
        this.id = name + "@" + sender.getName();
    }

    /**
     * 接受时调用
     */
    @Override
    public void onAccept() {
        if (sender.isOnline()) acceptEvent();
        else {
            player.sendMessage("对方已下线");
        }
    }

    /**
     * 拒绝时调用
     */
    @Override
    public void onDeny() {
        if (sender.isOnline()) denyEvent();
        else {
            player.sendMessage("对方已下线");
        }
    }

    @Override
    public void onCanel() {
    }

    public boolean sendTo(Player p) {
        if (p.isOnline() && ReqestManage.addEvent(p.getName(),id,this)){
            TextComponent a1 = new TextComponent(" §f" + sender.getDisplayName() + " §7给你发送了一个 §f" + name + "§7 请求 §3§l>>§b§l点我接受§3§l<< ");
            a1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/confirm " + id));
            a1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§3点我或者使用指令§a/c " + id + "§3接受" + "\n" + "§3也可以使用指令§a/d " + id + "§9拒绝").color(ChatColor.BLUE).create()));
            p.spigot().sendMessage(a1);
            sender.sendMessage(" §b已向 §f" + p.getDisplayName() + " §b发送" + name + "请求");
            return true;
        }
        sender.sendMessage(" " + p.getDisplayName() + "§b已存在一个相同请求");
        return false;
    }

    public boolean send() {
        return sendTo(player);
    }

    public Player getPlayer() {
        return player;
    }

    public Player getSender() {
        return sender;
    }

    public abstract void acceptEvent();

    public void denyEvent() {
        sender.sendMessage(" §f" + player.getDisplayName() + "§r§b已拒绝§f" + name + "§b请求");
        player.sendMessage(" §b已拒绝§f" + name + "§b请求");
    }

}
