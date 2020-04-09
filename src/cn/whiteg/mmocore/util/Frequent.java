package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.Setting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Frequent {
    private static final Thread therad;
    public static MMOCore plugin;
    private static Map<String, Integer> playerEvents = Collections.synchronizedMap(new HashMap<>());

    static {
        plugin = MMOCore.plugin;
        therad = new Thread() {
            @Override
            public void run() {
                while (true) {
                    if (!plugin.isEnabled()) return;
                    playerEvents.clear();
                    try{
                        sleep(10000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        therad.start();
    }

    public static boolean CheckFrquent(String id,int i) {
        if (plugin.isEnabled())
            return CheckFrquent(id,i,Setting.Frequent);
        return true;
    }

    public static int CheckFrquentInt(String id,int i) {
        if (plugin.isEnabled()){
            int pi = playerEvents.getOrDefault(id,0);
            playerEvents.put(id,pi += i);
            return pi;
        }
        return i;
    }

    public static boolean CheckFrquent(String id,int i,int max) {
        if (plugin.isEnabled()){
            return CheckFrquentInt(id,i) > max;
        }
        return true;
    }

    public static Thread getTherad() {
        return therad;
    }
}
