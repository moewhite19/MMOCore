package cn.whiteg.mmocore;

import cn.whiteg.mmocore.common.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCommand extends CommandInterface {
    final public List<String> allCommand = Arrays.asList("reload","confirm","deny","suicide","playsound","maintenance");
    final public Map<String, CommandInterface> commandMap = new HashMap<>(allCommand.size());
    final public SubCommand subCommand = new SubCommand();

    public MainCommand() {
        for (int i = 0; i < allCommand.size(); i++) {
            String cmd = allCommand.get(i);
            try{
                Class c = Class.forName("cn.whiteg.mmocore.commands.mainCommand." + cmd);
                CommandInterface ci = (CommandInterface) c.newInstance();
                commandMap.put(cmd,ci);
                PluginCommand pc = MMOCore.plugin.getCommand(cmd);
                if (pc != null){
                    pc.setExecutor(subCommand);
                    pc.setTabCompleter(subCommand);
                }
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
        if (commandMap.containsKey(args[0])){
            return commandMap.get(args[0]).onCommand(sender,cmd,label,args);
        } else {
            sender.sendMessage("无效指令");
        }
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length > 1){
            List ls = null;
            if (commandMap.containsKey(args[0])) ls = commandMap.get(args[0]).onTabComplete(sender,cmd,label,args);
            if (ls != null){
                return getMatches(args[args.length - 1],ls);
            }
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }
        if (args.length == 1){
            return getMatches(args[0],allCommand);
        }
        return null;
    }

    public void regCommand(String var1,CommandInterface cmd) {

    }

    public class SubCommand extends CommandInterface {

        @Override
        public boolean onCommand(CommandSender commandSender,Command command,String s,String[] strings) {
            final CommandInterface ci = commandMap.get(command.getName());
            if (ci == null) return false;
            String[] args = new String[strings.length + 1];
            args[0] = command.getName();
            System.arraycopy(strings,0,args,1,strings.length);
            ci.onCommand(commandSender,command,s,args);
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender commandSender,Command command,String s,String[] strings) {
            CommandInterface ci = commandMap.get(command.getName());
            if (ci == null) return null;
            String[] args = new String[strings.length + 1];
            args[0] = command.getName();
            System.arraycopy(strings,0,args,1,strings.length);
            return ci.onTabComplete(commandSender,command,s,args);
        }
    }
}
