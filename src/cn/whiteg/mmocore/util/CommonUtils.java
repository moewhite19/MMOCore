package cn.whiteg.mmocore.util;

import java.util.regex.Pattern;

public class CommonUtils {
    private final static Pattern MC_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");
    public static boolean checkName(String str) {
        return MC_USERNAME_PATTERN.matcher(str).matches();
    }
}
