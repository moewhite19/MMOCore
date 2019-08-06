package cn.whiteg.mmocore.container;

public abstract class ReqestAbs implements Reqest {
    final Long time;
    private ReqestContainer h;

    public ReqestAbs() {
        time = System.currentTimeMillis();
    }

    @Override
    public void remove() {
        if (h != null) h.remove(this);
    }

    @Override
    public void accept() {
        remove();
        onAccept();
    }

    @Override
    public void deny() {
        remove();
        onDeny();
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
        onCanel();
    }

    @Override
    public boolean isOvertime() {
        return System.currentTimeMillis() - time > 60000;
    }

    abstract public void onAccept();

    abstract public void onDeny();

    public void onCanel() {

    }

}
