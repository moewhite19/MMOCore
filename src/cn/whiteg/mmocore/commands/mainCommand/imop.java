package cn.whiteg.mmocore.commands.mainCommand;

import cn.whiteg.mmocore.CommandInterface;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class imop extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (sender.hasPermission("whiteg.test")){
            if (args.length == 2){
                if (args[1].equals("esshomes")){
                    File dir = new File(MMOCore.plugin.getDataFolder(),"Player");
                    for (File file : Objects.requireNonNull(dir.listFiles())) {
                        MMOCore.logger.info("导入" + file.getName());
                        File ef = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata" + File.separator + file.getName());
                        YamlConfiguration con = YamlConfiguration.loadConfiguration(file);
                        YamlConfiguration ec = YamlConfiguration.loadConfiguration(ef);
                        ConfigurationSection homes = ec.getConfigurationSection("homes");
                        if (homes == null){
                            MMOCore.logger.info("跳过" + file.getName());
                            continue;
                        }
                        con.set("homes",homes);
                        try{
                            con.save(file);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }else {
                    sender.sendMessage("未知选项");
                }

            } else {
                sender.sendMessage("参数错误");
            }
        } else {
            sender.sendMessage("阁下没有权限");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return null;
    }
}
