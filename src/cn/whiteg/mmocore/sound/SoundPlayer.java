package cn.whiteg.mmocore.sound;

import cn.whiteg.mmocore.util.PluginUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SoundPlayer implements Sound, Runnable {
    final List<Sound> track;
    private final Plugin plugin;
    private final int tick; //播放间隔
    Set<Location> locations = new HashSet<>(); //正在播放的位置
    Map<Player, Location> players = new HashMap<>(); //播放的玩家列表，如果值为Null则实时获取玩家位置
    private int loop; //循环播放次数 如果为-1则无限循环
    private int progress = 0; //当前播放进度
    private BukkitTask task = null;

    public SoundPlayer(Plugin plugin,List<Sound> track,int loop,int tick) {
        this.plugin = plugin;
        this.track = track;
        this.loop = loop;
        this.tick = tick;
    }

    public static SoundPlayer load(Plugin plugin,ConfigurationSection cs) {
        List<?> rawList = cs.getList("track");
        if (rawList == null) throw new IllegalArgumentException("无效配置: " + cs.getName());
        List<Sound> list = new ArrayList<>(rawList.size());
        for (Object str : rawList) {
            list.add(Sound.parseYml(str));
        }
        return new SoundPlayer(plugin,list,cs.getInt("loop",1),cs.getInt("tick",1));
    }

    public static SoundPlayer load(ConfigurationSection cs) {
        return load(PluginUtil.getCurrentPlugin(),cs);
    }

    @Override
    public void playTo(Location location) {
        if (isEmpty()) return;
        locations.add(location);
        start();
    }

    @Override
    public void playTo(Player player) {
        if (isEmpty()) return;
        players.put(player,null);
        start();
    }

    @Override
    public void playTo(Player player,Location location) {
        if (isEmpty()) return;
        players.put(player,location);
        start();
    }

    @Override
    public void stopTo(Location location) {
        locations.remove(location);
    }

    @Override
    public void stopTo(Player player) {
        players.remove(player);
    }

    //是否为空
    public boolean isEmpty() {
        return track.isEmpty() || loop == 0;
    }

    @Override
    public void run() {
        if (progress >= track.size()){
            if (loop <= -1){ //无限循环
                progress = 0;
            } else { //循环
                if (--loop == 0){
                    //中止循环
                    cancel();
                    return;
                }
                progress = 0;
            }
        }

        Sound sound = track.get(progress);
        if (!sound.isEmpty()){
            Map<Player, Location> map = new HashMap<>(Bukkit.getOnlinePlayers().size());
            if (!locations.isEmpty()){
                for (Location location : locations) {
                    World world = location.getWorld();
                    if (world == null) return;
                    @NotNull Collection<Entity> entitys;
                    if (getVolume() > 16) entitys = world.getEntities();
                    else {
                        double r = getVolume() * 16;
                        entitys = world.getNearbyEntities(location,r,r,r);
                    }
                    for (Entity entity : entitys) {
                        if (entity instanceof Player) map.put((Player) entity,location);
                    }
                }
            } else if (!this.players.isEmpty()){
                Iterator<Map.Entry<Player, Location>> it = this.players.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Player, Location> entry = it.next();
                    if (!entry.getKey().isOnline()){
                        it.remove();
                    } else {
                        map.put(entry.getKey(),entry.getValue() != null ? entry.getValue() : entry.getKey().getLocation());
                    }
                }
            } else {
                cancel();
                return;
            }
            if (!map.isEmpty()){
                map.forEach(sound::playTo);
            }
        }
        progress++;
    }

    @Override
    public float getVolume() {
        if (track.isEmpty()) return 0F;
        return track.get(0).getVolume();
    }

    public synchronized void cancel() {
        if (isRun()){
            locations.clear();
            players.clear();
            progress = 0;
            Bukkit.getScheduler().cancelTask(task.getTaskId());
            task = null;
        }
    }

    public void start() {
        if (!isRun()){
            task = Bukkit.getScheduler().runTaskTimer(plugin,this,0,tick);
        }
    }

    public synchronized boolean isRun() {
        return task != null;
    }

    @Override
    public SoundPlayer clone() {
        return new SoundPlayer(plugin,track,loop,tick);
    }
}
