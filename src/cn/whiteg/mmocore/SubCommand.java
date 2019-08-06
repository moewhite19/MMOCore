package cn.whiteg.mmocore;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubCommand extends CommandInterface {
    final public String[] subCmds = new String[]{"confirm","deny"};
    final private Map<String, CommandInterface> cmds = new HashMap<>();

    public SubCommand() {
        for (String c : subCmds) {
            cmds.put(c,MMOCore.plugin.mainCommand.CommandMap.get(c));
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender,Command command,String s,String[] strings) {
        final CommandInterface ci = cmds.get(command.getName());
        if (ci == null) return false;
        String[] args = new String[strings.length + 1];
        args[0] = command.getName();
/*        for(int i = 0 ; i < args.length ; i++){
            args[i + 1] = strings[i] ;
        }*/
        System.arraycopy(strings,0,args,1,strings.length);
        ci.onCommand(commandSender,command,s,args);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender,Command command,String s,String[] strings) {
        CommandInterface ci = MMOCore.plugin.mainCommand.CommandMap.get(command.getName());
        if (ci == null) return null;
        String[] args = new String[strings.length + 1];
        args[0] = command.getName();
/*        for(int i = 0 ; i < args.length ; i++){
            args[i + 1] = strings[i] ;
        }*/

        System.arraycopy(strings,0,args,1,strings.length);
        return ci.onTabComplete(commandSender,command,s,args);
    }
}
