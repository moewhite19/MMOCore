package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CoolDownUtil {
    static BukkitTask task = null;
    private static Map<String, PlayerCd> map = new HashMap<>();

    public static void update() {
        if (map.isEmpty()){
            return;
        }

        try{
            Iterator<Map.Entry<String, PlayerCd>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, PlayerCd> e = it.next();
                PlayerCd cp = e.getValue();
                if (cp.isEmpty()) it.remove();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void runTask() {
        if (task != null) return;
        Bukkit.getScheduler().runTaskTimerAsynchronously(MMOCore.plugin,CoolDownUtil::update,100,100);
    }

    //设置cd 单位毫秒
    public static void setCd(String id,String name,int time) {
//        PlayerCd cp = cdmap.get(id);
//        if (cp == null){
//            cp = new PlayerCd();
//            cdmap.put(id,cp);
//        }
        PlayerCd cp = map.computeIfAbsent(id,(k) -> {
            return new PlayerCd();
        });
        cp.setCd(name,time);
    }

    public static void setCds(String id,String name,int time) {
        setCd(id,name,time * 1000);
    }

    public static PlayerCd get(String id) {
        PlayerCd cd = map.get(id);
        if (cd == null) return null;
        if (cd.cdp.isEmpty()){
            map.remove(id);
            return null;
        }
        return cd;
    }

    public static int getCd(String id,String name) {
        PlayerCd cp = map.get(id);
        if (cp == null) return 0;
        return cp.getCd(name);
    }

    public static int getCds(String id,String name) {
        return getCd(id,name) / 1000;
    }

    public static boolean hasCd(String id,String name) {
        PlayerCd cp = map.get(id);
        if (cp == null) return true;
        return cp.hasCd(name);
    }

    public static class PlayerCd {
        Map<String, Long> cdp = new HashMap<>();

        PlayerCd() {
        }

        public int getCd(String name) {
            Long l = cdp.get(name);
            return l == null ? 0 : (int) (l - System.currentTimeMillis());
        }

        public int getCds(String name) {
            return getCd(name) / 1000;
        }

        public void setCd(String name,int time) {
            cdp.put(name,System.currentTimeMillis() + time);
        }

        public void setCds(String name,int time) {
            setCd(name,time * 1000);
        }

        public void remove(String name) {
            cdp.remove(name);
        }

        public boolean hasCd(String name) {
            Long l = cdp.get(name);
            if (l == null) return true;
            if (l < System.currentTimeMillis()){
                remove(name);
                return true;
            }
            return false;
        }

        public Set<String> getKeys() {
            return cdp.keySet();
        }

        public boolean isEmpty() {
            if (cdp.isEmpty()) return true;
            for (String n : getKeys()) {
                hasCd(n);
            }
            return cdp.isEmpty();
        }
    }
}
