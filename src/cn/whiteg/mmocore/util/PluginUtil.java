package cn.whiteg.mmocore.util;

import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;

public class PluginUtil {

    public static void kickPlayer(Player p,String Message) {
        p.kickPlayer(Message);
    }

    public static PluginCommand getPluginCommanc(final JavaPlugin plugin,final String name) {
        return getPluginCommand(plugin,name);
    }

    public static PluginCommand getPluginCommand(final JavaPlugin plugin,final String name) {
        PluginCommand pc = plugin.getCommand(name);
        if (pc == null){
            try{
                final Constructor<PluginCommand> cr = PluginCommand.class.getDeclaredConstructor(String.class,Plugin.class);
                cr.setAccessible(true);
                pc = cr.newInstance(name,plugin);
                pc.setDescription("None " + name);
                pc.setUsage("/" + name);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        return pc;
    }
}
