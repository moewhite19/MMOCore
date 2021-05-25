package cn.whiteg.mmocore.sound;

import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public interface Sound {
    //空音效
    SingleSound EMPTY = new SingleSound(null,0F,0F);


    //旧方法名
    @Deprecated
    static Sound parseSound(Object object) {
        return parseYml(object);
    }

    //从配置文件加载音效
    static Sound parseYml(Object object) {
        if (object == null) return EMPTY;
        if (object instanceof String){
            return SingleSound.load((String) object);
        } else if (object instanceof List){
            return CompoundSound.load((List<?>) object);
        } else if (object instanceof ConfigurationSection){
            return SoundPlayer.load((ConfigurationSection) object);
        }
        return EMPTY;
    }


    void playTo(Location location);

    void playTo(Player player);

    void playTo(Player player,Location location);

    void stopTo(Location location);

    void stopTo(Player player);

    boolean isEmpty();

    float getVolume();

    Sound clone();
}
