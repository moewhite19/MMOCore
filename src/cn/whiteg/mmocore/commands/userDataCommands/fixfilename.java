package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.common.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class fixfilename extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (!sender.hasPermission("whiteg.test")){
            sender.sendMessage("§b权限不足");
            return true;
        }
        File dir = Setting.DataDir;
        if (!dir.isDirectory()){
            sender.sendMessage("文件夹类型出错");
        }
        for (File f : dir.listFiles()) {
            try{
                YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
                String name = y.getString("Player.uuid");
                if (name == null || name.isEmpty()) continue;
                File newFile = new File(f.getParentFile(),name.toLowerCase() + ".yml");
                f.renameTo(newFile);
            }catch (Exception e){
                sender.sendMessage("重命名出错" + f);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
