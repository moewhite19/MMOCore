package cn.whiteg.mmocore.util;

import java.util.regex.Pattern;

public class CommonUtils {
    private final static long day = 86400000L;
    private final static long hour = 3600000L;
    private final static long minute = 60000L;
    private final static long second = 1000L;


    private final static Pattern MC_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    public static boolean checkName(String str) {
        return MC_USERNAME_PATTERN.matcher(str).matches();
    }

    public static long getTime(String str) {
        if (str.isEmpty()) return 0;
        try{
            char e = str.charAt(str.length() - 1);
            switch (e) {
                case 'S','s' -> {
                    str = str.substring(0,str.length() - 1);
                    return Long.parseLong(str) * second;
                }
                case 'M','m' -> {
                    str = str.substring(0,str.length() - 1);
                    return Long.parseLong(str) * minute;
                }
                case 'H','h' -> {
                    str = str.substring(0,str.length() - 1);
                    return Long.parseLong(str) * hour;
                }
                case 'D','d' -> {
                    str = str.substring(0,str.length() - 1);
                    return Long.parseLong(str) * day;
                }
            }
            return Long.parseLong(str) * 60000;
        }catch (NumberFormatException ex){
            return -1;
        }
    }

    public static String tanMintoh(long l) {
        final StringBuilder sb = new StringBuilder();
        if (l < 0) return "";
        if (l < second){
            return sb.append(l).append("毫秒").toString();
        }
        if (l >= day){
            int i = 0;
            while (l >= day) {
                l -= day;
                i++;
            }
            sb.append(i).append("天");
        }
        if (l >= hour){
            int i = 0;
            while (l >= hour) {
                l -= hour;
                i++;
            }
            sb.append(i).append("小时");
        }
        if (l >= minute){
            int i = 0;
            while (l >= minute) {
                l -= minute;
                i++;
            }
            sb.append(i).append("分钟");
        }
        if (l >= second){
            int i = 0;
            while (l >= second) {
                l -= second;
                i++;
            }
            sb.append(i).append("秒");
        }
        return sb.toString();
    }
}
