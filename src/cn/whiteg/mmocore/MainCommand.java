package cn.whiteg.mmocore;

import org.bukkit.command.*;

import java.util.*;

public class MainCommand extends CommandInterface {
    public Map<String, CommandInterface> CommandMap = new HashMap();
    public List<String> AllCmd;

    public MainCommand() {
        AllCmd = Arrays.asList("reload","confirm","deny","suicide");
        for (int i = 0; i < AllCmd.size(); i++) {
            try{
                Class c = Class.forName("cn.whiteg.mmocore.commands.mainCommand." + AllCmd.get(i));
                regCommand(AllCmd.get(i),(CommandInterface) c.newInstance());
            }catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0){
            sender.sendMessage("§2[§bMMOCore§2]");
            return true;
        }
        if (CommandMap.containsKey(args[0])){
            return CommandMap.get(args[0]).onCommand(sender,cmd,label,args);
        } else {
            sender.sendMessage("无效指令");
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length > 1){
            List ls = null;
            if (CommandMap.containsKey(args[0])) ls = CommandMap.get(args[0]).onTabComplete(sender,cmd,label,args);
            if (ls != null){
                return getMatches(args[args.length - 1],ls);
            }
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }
        if (args.length == 1){
            return getMatches(args[0],AllCmd);
        }
        return null;
    }

    public void regCommand(String var1,CommandInterface cmd) {
        CommandMap.put(var1,cmd);
    }

}
