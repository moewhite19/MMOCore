package cn.whiteg.mmocore.event;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class DataConCreateEvent extends CallEvent  implements Cancellable {
    private static HandlerList handler = new HandlerList();
    final private DataCon dataCon;

    public DataConCreateEvent(DataCon dataCon) {
        this.dataCon = dataCon;
    }

    public static HandlerList getHandlerList() {
        return handler;
    }

    public DataCon getDataCon() {
        return dataCon;
    }

    @Override
    public HandlerList getHandlers() {
        return handler;
    }
}
