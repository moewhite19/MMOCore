package cn.whiteg.mmocore.sound;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompoundSound implements Sound {

    private final List<Sound> list;

    public CompoundSound(List<Sound> list) {
        this.list = list;
    }

    public static CompoundSound load(List<?> args) {
        List<Sound> list = new ArrayList<>(args.size());
        for (Object str : args) {
            if (str instanceof String){
                list.add(SingleSound.load((String) str));
            } else list.add(Sound.parseYml(str));
        }
        return new CompoundSound(list);
    }

    //在指定位置播放
    @Override
    public void playTo(Location location) {
        if (isEmpty()) return;
        for (Sound sound : list) {
            sound.playTo(location);
        }
    }

    //播放给玩家
    @Override
    public void playTo(Player player) {
        playTo(player,player.getLocation());
    }

    //播放给玩家
    @Override
    public void playTo(Player player,Location location) {
        if (isEmpty()) return;
        for (Sound sound : list) {
            sound.playTo(player,location);
        }
    }

    //停止播放
    @Override
    public void stopTo(Player player) {
        if (isEmpty()) return;
        for (Sound sound : list) {
            sound.playTo(player);
        }
    }

    @Override
    public void stopTo(Location location) {
        if (isEmpty()) return;
        Collection<Entity> entitys;
        float volume = getVolume();
        World world = location.getWorld();
        if (world == null) return;
        if (volume > 16) entitys = world.getEntities();
        else {
            double r = volume * 16;
            entitys = world.getNearbyEntities(location,r,r,r);
        }
        for (Entity entity : entitys) {
            if (entity instanceof Player){
                for (Sound sound : list) {
                    sound.playTo((Player) entity);
                }
            }
        }
    }

    //是否为空
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public float getVolume() {
        if (list.isEmpty()) return 0F;
        return list.get(0).getVolume();
    }

    @Override
    public Sound clone() {
        return new CompoundSound(list);
    }

}
