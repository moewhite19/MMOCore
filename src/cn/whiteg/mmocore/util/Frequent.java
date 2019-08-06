package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Frequent {
    private static final BukkitTask task;
    private static Map<String, Integer> playerEvents = new ConcurrentHashMap<>();

    static {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                playerEvents.clear();
            }
        }.runTaskTimerAsynchronously(MMOCore.plugin,200,200);
    }

    public static boolean CheckFrquent(String id,int i) {
        return CheckFrquent(id,i,Setting.Frequent);
    }

    public static boolean CheckFrquent(String id,int i,int max) {
        int pi = playerEvents.getOrDefault(id,0);
        playerEvents.put(id,pi += i);
        if (pi > max){
            return true;
        }
        return false;
    }
}
