package cn.whiteg.mmocore.api;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.container.ConfirmReqest;
import cn.whiteg.mmocore.container.Reqest;
import cn.whiteg.mmocore.container.ReqestContainer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReqestManage {
    public static Map<String, ReqestContainer> confirmMap = new HashMap<>();
    private static BukkitTask runner;

    public static void clearPlayerConfirm(String id) {
        confirmMap.remove(id);
    }

    public static boolean removeEvent(String id) {
        final ReqestContainer ev = getContainer(id);
        if (ev == null) return false;
        ev.removeEvent();
        return true;
    }

    public static boolean removeEvent(String id,String name) {
        final ReqestContainer ev = getContainer(id);
        if (ev == null) return false;
        ev.removeEvent(name);
        if (ev.isEmpty()){
            clearPlayerConfirm(id);
        }
        return true;
    }

    public static void setEvent(final String id,final String name,final Reqest reqest) {
        final ReqestContainer cf = getContainer(id,true);
        cf.addEvent(name,reqest);
        start();
    }

    public static boolean addEvent(final String id,final String name,final Reqest reqest) {
        final ReqestContainer cf = getContainer(id,true);
        if (cf.addEvent(name,reqest)){
            start();
            return true;
        }
        return false;
    }

    public static boolean addEvent(final String id,final String name,final Runnable runnable) {
        return addEvent(id,name,new ConfirmReqest(runnable));
    }


    public static Reqest getEvent(final String id,final String name) {
        final ReqestContainer cf = getContainer(id);
        if (cf == null) return null;
        return cf.getRequest(name);
    }

    @Deprecated
    public static boolean runTask(final String id) {
        final ReqestContainer ce = getContainer(id);
        if (ce == null) return false;
        final Reqest la = ce.getLastEvn();
        if (la == null) return false;
        la.accept();
        la.remove();
        return true;
    }

    public static boolean runTask(final String id,final String name) {
        Reqest ce = getEvent(id,name);
        if (ce == null) return false;
        ce.accept();
        ce.remove();
        return true;
    }


    public static ReqestContainer getContainer(final String id) {
        return getContainer(id,false);
    }

    public static ReqestContainer getContainer(final String id,final boolean create) {
        ReqestContainer ce = confirmMap.get(id);
        if (ce == null && create){
            ce = new ReqestContainer(id);
            confirmMap.put(id,ce);
        }
        return ce;
    }

    public static void start() {
        if (runner == null){
            runner = Bukkit.getScheduler().runTaskTimerAsynchronously(MMOCore.plugin,() -> {
                if (confirmMap.isEmpty()){
                    runner.cancel();
                    runner = null;
                    return;
                }
                Iterator<Map.Entry<String, ReqestContainer>> i = confirmMap.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry<String, ReqestContainer> e = i.next();
                    if (e.getValue().candel()) i.remove();
                }
            },200,200);
        }
    }
}
