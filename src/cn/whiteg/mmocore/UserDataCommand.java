package cn.whiteg.mmocore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class UserDataCommand implements CommandExecutor, TabCompleter {
    private Map<String, CommandInterface> CommandMap = new HashMap();
    private String[] AllCmd;
    public UserDataCommand() {
        AllCmd = new String[]{"load","reload","save","get","set","list","delete","fixfilename","delete","getuuid","unload" , "create", "rename" , "recovery"};
        for (int i = 0; i < AllCmd.length; i++) {
            try{
                Class c = Class.forName("cn.whiteg.mmocore.commands.userDataCommands." + AllCmd[i]);
                regCommand(AllCmd[i],(CommandInterface) c.newInstance());
            }catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if(args.length == 0){
            sender.sendMessage("§2[§bMMOCore§2]");
            return true;
        }
        if (CommandMap.containsKey(args[0])){
            return CommandMap.get(args[0]).onCommand(sender,cmd,label,args);
        }  else {
            sender.sendMessage("未知参数");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length > 1){
            if (CommandMap.get(args[0])!=null) return CommandMap.get(args[0]).onTabComplete(sender,cmd,label,args);
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }
        if (args.length == 1){
            return getMatches(args[0],Arrays.asList(AllCmd));
        }
        return null;
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

    public void regCommand(String var1,CommandInterface cmd) {
        CommandMap.put(var1,cmd);
    }
}
