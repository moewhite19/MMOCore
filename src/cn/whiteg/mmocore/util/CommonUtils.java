package cn.whiteg.mmocore.util;

import java.util.regex.Pattern;

public class CommonUtils {
    private final static Pattern MC_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    public static boolean checkName(String str) {
        return MC_USERNAME_PATTERN.matcher(str).matches();
    }

    public static long getTime(String str) {
        if (str.isEmpty()) return 0;
        try{
            char e = str.charAt(str.length() - 1);
            switch (e) {
                case 'S':
                case 's': {
                    str = str.substring(0,str.length() - 1);
                    return Long.valueOf(str) * 1000;
                }
                case 'M':
                case 'm': {
                    str = str.substring(0,str.length() - 1);
                    return Long.valueOf(str) * 60000;
                }
                case 'H':
                case 'h': {
                    str = str.substring(0,str.length() - 1);
                    return Long.valueOf(str) * 3600000;
                }
                case 'D':
                case 'd': {
                    str = str.substring(0,str.length() - 1);
                    return Long.valueOf(str) * 86400000;
                }
            }
            return Long.valueOf(str) * 60000;
        }catch (NumberFormatException ex){
            return 0;
        }
    }

    public static String tanMintoh(long l) {
        final StringBuilder sb = new StringBuilder();
        if (l < 1000){
            return sb.append(l).append("毫秒").toString();
        }
        int s = 0;
        int m = 0;
        int h = 0;
        int d = 0;
        while (l >= 1000) {
            l -= 1000;
            s++;
            if (s >= 60){
                s = 0;
                m++;
            }
            if (m >= 60){
                m = 0;
                h++;
            }
            if (h >= 24){
                h = 0;
                d++;
            }
        }
        if (d > 0) sb.append(d).append("天");
        if (h > 0) sb.append(h).append("小时");
        if (m > 0) sb.append(m).append("分钟");
        if (s > 0) sb.append(s).append("秒");
        return sb.toString();
    }
}
