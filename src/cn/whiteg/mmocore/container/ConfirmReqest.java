package cn.whiteg.mmocore.container;

public class ConfirmReqest extends ReqestAbs {
    final Runnable c;

    public ConfirmReqest(final Runnable r) {
        c = r;
    }


    @Override
    public void onAccept() {
        c.run();
    }

    @Override
    public void onDeny() {

    }

    @Override
    public void onCanel() {

    }
}
