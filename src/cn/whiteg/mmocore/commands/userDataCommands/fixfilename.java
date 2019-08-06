package cn.whiteg.mmocore.commands.userDataCommands;

import cn.whiteg.mmocore.Setting;
import cn.whiteg.mmocore.common.HasCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class fixfilename extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
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
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }

    @Override
    public String getDescription() {
        return "修复出错的文件名(当服务器切换正版/盗版服务器时可能需要用到)";
    }
}
