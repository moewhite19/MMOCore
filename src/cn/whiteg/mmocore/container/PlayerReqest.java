package cn.whiteg.mmocore.container;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.api.ReqestManage;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
            TextComponent a1 = new TextComponent(" §f" + sender.getDisplayName() + " §7给你发送了一个 §f" + name + "§7 请求 §3§l>>§b§l点我接受§3§l<< ");
            a1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/confirm " + id));
            a1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new ComponentBuilder("§3点我或者使用指令§a/c " + id + "§3接受" + "\n" + "§3也可以使用指令§a/d " + id + "§9拒绝").color(ChatColor.BLUE).create()));
            p.spigot().sendMessage(a1);
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
            if(p == sender) continue;
            ComponentBuilder cb = new ComponentBuilder(" §f" + sender.getDisplayName() + " §7给你发送了一个 §f" + name + "§7 请求");
            cb.append(" §3§l>>§b§l点我接受§3§l<<");
            cb.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/confirm " + id));
            cb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[]{new TextComponent("§3点我或者使用指令§a/c " + id + "§3接受" + "\n" + "§3也可以使用指令§a/d " + id + "§9拒绝")}));
            BaseComponent[] msg = cb.create();
            if (p.isOnline() && ReqestManage.addEvent(p.getName(),id,this)){
                p.sendMessage(msg);
                playsound(p);
                i++;
            } else {
                sender.sendMessage(" " + p.getDisplayName() + "§b已存在一个相同请求");
            }
        }
        sender.sendMessage(" §b已发送 §f" + i + " §b个" + name + "请求");
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
        new BukkitRunnable() {
            int i = 0;
            float[] list = new float[]{0f,0.6f,0.4f};

            @Override
            public void run() {
                if (p.isDead()){
                    cancel();
                    return;
                }
                float f = list[i];
                if (f < 0f || f > 2f) return;
                Location loc = p.getLocation();
                p.playSound(loc,"block.note_block.bell",SoundCategory.PLAYERS,1,f);
                i++;
                if (i >= list.length){
                    cancel();
                }
            }
        }.runTaskTimer(MMOCore.plugin,0,2);
    }

    public void remove(CommandSender s) {
        ReqestContainer c = ReqestManage.getContainer(s.getName(),false);
        if (c == null) return;
        c.remove(this);
    }
}
