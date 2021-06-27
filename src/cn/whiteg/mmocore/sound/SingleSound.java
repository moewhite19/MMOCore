package cn.whiteg.mmocore.sound;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class SingleSound implements Sound {
    final String sound;
    final float volume;
    final float pitch;
    final SoundCategory category;

    public SingleSound(String str) {
        String[] args = str.split(" ");
        if (args.length > 0) sound = args[0];
        else sound = "";
        if (args.length > 1) volume = Float.parseFloat(args[1]);
        else volume = 1F;
        if (args.length > 2) pitch = Float.parseFloat(args[2]);
        else pitch = 1F;
        if (args.length > 3) category = SoundCategory.valueOf(args[3].toUpperCase());
        else category = SoundCategory.PLAYERS;
    }

    public SingleSound(String name,float volume) {
        this.sound = name;
        this.volume = volume;
        this.pitch = 1F;
        this.category = SoundCategory.PLAYERS;
    }

    public SingleSound(String name,float volume,float pitch) {
        this.sound = name;
        this.volume = volume;
        this.pitch = pitch;
        this.category = SoundCategory.PLAYERS;
    }

    public SingleSound(String name,float volume,float pitch,SoundCategory soundCategory) {
        this.sound = name;
        this.volume = volume;
        this.pitch = pitch;
        this.category = soundCategory;
    }

    public static SingleSound load(String str) {
        if (str == null || str.isEmpty()) return Sound.EMPTY;
        try{
            return new SingleSound(str);
        }catch (Exception e){
            e.printStackTrace();
            return Sound.EMPTY;
        }
    }

    //在指定位置播放
    public void playTo(Location location) {
        if (isEmpty()) return;
        @Nullable World world = location.getWorld();
        if (world != null) world.playSound(location,sound,SoundCategory.PLAYERS,volume,pitch);
    }

    //播放给玩家
    public void playTo(Player player) {
        playTo(player,player.getLocation());
    }

    //播放给玩家
    public void playTo(Player player,Location location) {
        if (isEmpty()) return;
        player.playSound(location,sound,SoundCategory.PLAYERS,volume,pitch);
    }

    //播放给玩家
    public void stopTo(Player player) {
        if (isEmpty()) return;
        player.stopSound(sound,SoundCategory.PLAYERS);
    }

    //播放到指定位置
    public void stopTo(Location location) {
        if (isEmpty()) return;
        Collection<Entity> entitys;
        World world = location.getWorld();
        if (world == null) return;
        if (volume > 16) entitys = world.getEntities();
        else {
            double r = volume * 32;
            entitys = world.getNearbyEntities(location,r,r,r);
        }
        for (Entity entity : entitys) {
            if (entity instanceof Player){
                ((Player) entity).stopSound(sound,SoundCategory.PLAYERS);
            }
        }
    }

    //是否为空
    public boolean isEmpty() {
        return sound == null || sound.isEmpty();
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public Sound clone() {
        return this;
    }
}
