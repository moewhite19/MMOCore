package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import com.bekvon.bukkit.residence.containers.cmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class imop extends HasCommandInterface {

    @Override
    public boolean executor(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            if (args[0].equals("esshomes")){
                File dir = new File(MMOCore.plugin.getDataFolder(),"Player");
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    MMOCore.logger.info("导入" + file.getName());
                    File ef = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata" + File.separator + file.getName());
                    YamlConfiguration con = YamlConfiguration.loadConfiguration(file);
                    YamlConfiguration ec = YamlConfiguration.loadConfiguration(ef);
                    ConfigurationSection homes = ec.getConfigurationSection("homes");
                    if (homes != null){
                        con.set("homes",homes);
                    }
                    try{
                        con.save(file);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            } else {
                sender.sendMessage("未知选项");
            }

        } else {
            sender.sendMessage("参数错误");
        }
        return false;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }
}
