package cn.whiteg.mmocore.container;

import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public abstract class ReqestAbs implements Reqest {
    final Long time;
    private ReqestContainer h;

    public ReqestAbs() {
        time = System.currentTimeMillis() + 60000;
    }

    @Override
    public synchronized void remove() {
        if (h != null) h.remove(this);
    }

    @Override
    public void accept(CommandSender sender) {
        remove();
        onAccept(sender);
    }

    @Override
    public void deny(CommandSender sender) {
        remove();
        onDeny(sender);
    }

    @Override
    public ReqestContainer getHander() {
        return h;
    }

    @Override
    public void setHander(ReqestContainer hander) {
        h = hander;
    }

    @Override
    public void canel() {
        remove();
        Bukkit.getScheduler().runTask(MMOCore.plugin,this::onCanel);
    }

    @Override
    public boolean isOvertime() {
        return System.currentTimeMillis() > time;
    }


    abstract public void onAccept(CommandSender sender);

    abstract public void onDeny(CommandSender sender);

    public void onCanel() {

    }

}
