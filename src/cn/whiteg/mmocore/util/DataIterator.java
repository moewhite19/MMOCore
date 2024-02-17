package cn.whiteg.mmocore.util;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DataIterator implements Iterator<DataCon> {

    private final File[] files;
    public int i = 0;
    public DataCon data = null;

    public DataIterator(File[] files) {
        this.files = files;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return i < files.length;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public DataCon next() {
        data = new DataCon(files[i]);
        final DataCon cacheData = MMOCore.getPlayerDataMap().get(data.getUUID());
        if (cacheData != null) data = cacheData;
        i++;
        return data;
    }

    public DataCon getData() {
        return data;
    }

    @Override
    public void remove() {
        if (data != null){
            FileMan.delete(Bukkit.getConsoleSender(),data);
        }
//        else {
//            throw new IllegalStateException();
//        }
    }
}
