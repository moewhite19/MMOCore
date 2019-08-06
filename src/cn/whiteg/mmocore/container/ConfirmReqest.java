package cn.whiteg.mmocore.container;

import org.bukkit.command.CommandSender;

public class ConfirmReqest extends ReqestAbs {
    final Runnable c;

    public ConfirmReqest(final Runnable r) {
        c = r;
    }


    @Override
    public void onAccept(CommandSender sender) {
        c.run();
    }

    @Override
    public void onDeny(CommandSender sender) {

    }

    @Override
    public void onCanel() {

    }
}
