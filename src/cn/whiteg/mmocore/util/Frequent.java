package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.Setting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Frequent {
    private static final Thread therad;
    private static Map<String, Integer> playerEvents = Collections.synchronizedMap(new HashMap<>());

    static {
        therad = new Thread() {
            @Override
            public void run() {
                while (true) {
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
        return CheckFrquent(id,i,Setting.Frequent);
    }

    public static boolean CheckFrquent(String id,int i,int max) {
        int pi = playerEvents.getOrDefault(id,0);
        playerEvents.put(id,pi += i);
        return pi > max;
    }

    public static Thread getTherad() {
        return therad;
    }
}
