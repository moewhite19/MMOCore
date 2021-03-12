package cn.whiteg.mmocore;

import cn.whiteg.mmocore.common.CommandInterface;
import cn.whiteg.mmocore.util.PluginUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDataCommand implements CommandExecutor, TabCompleter {
    final private HashMap<String, CommandInterface> commandMap;

    public UserDataCommand() {
        HashMap<String, CommandInterface> map = new HashMap<>();
        try{
            List<String> urls = PluginUtil.getUrls(getClass().getClassLoader(),false);
            for (String url : urls) {
                if (url.startsWith("cn/whiteg/mmocore/commands/userDataCommands/")){
                    int i = url.indexOf(".class");
                    if (i == -1) continue;
                    String path = url.replace('/','.').substring(0,i);
                    try{
                        Class<?> clazz = Class.forName(path);
                        if (CommandInterface.class.isAssignableFrom(clazz)){
                            String name = clazz.getSimpleName();
                            CommandInterface ci = (CommandInterface) clazz.newInstance();
                            map.put(name,ci);
                        }
                    }catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        commandMap = new HashMap<>(map);
    }

    public static List<String> getMatches(String value,List<String> list) {
        List<String> result = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            String str = list.get(i);
            if (str.startsWith(value)){
                result.add(str);
            }
        }
        return result;
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0){
            sender.sendMessage("§2[§bMMOCore§2]");
            return true;
        }
        if (commandMap.containsKey(args[0])){
            return commandMap.get(args[0]).onCommand(sender,cmd,label,args);
        } else {
            sender.sendMessage("未知参数");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length > 1){
            if (commandMap.get(args[0]) != null) return commandMap.get(args[0]).onTabComplete(sender,cmd,label,args);
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }
        if (args.length == 1){
            return getMatches(args[0],getCanUseCommands(sender));
        }
        return null;
    }

    //获取玩家可用指令列表
    public List<String> getCanUseCommands(CommandSender sender) {
        List<String> list = new ArrayList<>(commandMap.size());
        commandMap.forEach((key,ci) -> {
            if (ci.canUseCommand(sender)) list.add(key);
        });
        return list;
    }
}
