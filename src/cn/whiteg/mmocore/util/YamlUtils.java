package cn.whiteg.mmocore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


public class YamlUtils {
    public static void setLocation(FileConfiguration con,String path,Location loc) {
        if (!path.endsWith(".")){
            path = path + ".";
        }
        con.set(path + "world",loc.getWorld().getName());
        con.set(path + "x",loc.getX());
        con.set(path + "y",loc.getY());
        con.set(path + "z",loc.getZ());
        con.set(path + "yaw",loc.getYaw());
        con.set(path + "pitch",loc.getPitch());
    }

    public static void setLocation(ConfigurationSection con,Location loc) {
        if (con == null) return;
        con.set("world",loc.getWorld().getName());
        con.set("x",loc.getX());
        con.set("y",loc.getY());
        con.set("z",loc.getZ());
        con.set("yaw",loc.getYaw());
        con.set("pitch",loc.getPitch());
    }

    public static Location getLocation(FileConfiguration con,String path) {
        if (!path.endsWith(".")){
            path = path + ".";
        }
        String w = con.getString(path + "world");
        if (w == null) return null;
        World world = Bukkit.getWorld(w);
        if (world == null){
            return null;
        }
        return new Location(world,con.getDouble(path + "x"),con.getDouble(path + "y"),con.getDouble(path + "z"),(float) con.getDouble(path + "yaw",0),(float) con.getDouble(path + "pitch",0));
    }

    public static Location getLocation(ConfigurationSection con) {
        if (con == null) return null;
        World world = Bukkit.getWorld(con.getString("world"));
        if (world == null){
            return null;
        }
        Location location = new Location(world,con.getDouble("x"),con.getDouble("y"),con.getDouble("z"),(float) con.getDouble("yaw",0),(float) con.getDouble("pitch",0));
        return location;
    }
}
