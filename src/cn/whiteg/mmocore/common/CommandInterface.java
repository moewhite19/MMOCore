package cn.whiteg.mmocore.common;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class CommandInterface implements CommandExecutor, TabCompleter {


    public static List<String> getMatches(String[] args,List<String> list) {
        if (args.length == 0) return list;
        return getMatches(args[args.length - 1],list);
    }

    public static List<String> getMatches(List<String> list,String[] args) {
        if (args.length == 0) return list;
        return getMatches(args[args.length - 1],list);
    }

    public static List<String> getMatches(List<String> list,String value) {
        return getMatches(value,list);
    }

    public static List<String> getMatches(String value,List<String> list) {
        if (value == null || value.isEmpty()) return list;
        List<String> result = new ArrayList<>(list.size());
        for (String enter : list) {
            if (enter.intern().toLowerCase().startsWith(value.toLowerCase())){
                result.add(enter);
            }
        }
        return result;
    }

    @Deprecated
    public static List<String> PlayersList(String[] arg) {
        return getPlayersList(arg);
    }

    //获取玩家列表,根据arg筛选
    public static List<String> getPlayersList() {
        Collection<? extends Player> collection = Bukkit.getOnlinePlayers();
        List<String> players = new ArrayList<>(collection.size());
        for (Player p : collection) players.add(p.getName());
        return players;
    }

    //获取玩家列表,根据args筛选
    public static List<String> getPlayersList(String[] args) {
        return getMatches(getPlayersList(),args);
    }

    //获取玩家列表，根据args筛选，排除自己
    public static List<String> getPlayersList(String[] args,CommandSender sender) {
        List<String> list = getPlayersList(args);
        list.remove(sender.getName());
        return list;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return getPlayersList(args);
    }

    //获取指令名称
    public String getName() {
        return getClass().getSimpleName().toLowerCase();
    }

    //获取指令介绍
    public String getDescription() {
        return "";
    }

    //发送者是否可以使用指令
    public boolean canUseCommand(CommandSender sender) {
        return true;
    }
}
