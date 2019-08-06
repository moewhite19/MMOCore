package cn.whiteg.mmocore.container;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ReqestContainer {
    final private Map<String, Reqest> entMap = new HashMap<>();
    final private String name;
    private String lastEvn;

    public ReqestContainer(String name) {
        this.name = name;
    }

    public boolean addEvent(final String name,final Reqest reqset) {
        if (entMap.containsKey(name)) return false;
        setEvent(name,reqset);
        return true;
    }

    public void setEvent(final String name,final Reqest reqest) {
        entMap.put(name,reqest);
        lastEvn = name;
        reqest.setHander(this);
    }

    public Set<String> getKeys() {
        return entMap.keySet();
    }

    public void removeEvent(String name) {
        entMap.remove(name);
    }

    public void removeEvent() {
        entMap.remove(lastEvn);
    }

    public boolean isEmpty() {
        return entMap.isEmpty();
    }

    public Reqest getLastEvn() {
        return entMap.get(lastEvn);
    }

    public Reqest getRequest(String name) {
        return entMap.get(name);
    }

    public void remove(Reqest reqest) {
        Iterator<Entry<String, Reqest>> i = entMap.entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, Reqest> e = i.next();
            if (e.getValue() == reqest) i.remove();
            return;
        }
    }

    public String getName() {
        return name;
    }

    public boolean candel() {
        if (entMap.isEmpty()) return true;
        final Iterator<Entry<String, Reqest>> i = entMap.entrySet().iterator();
        while (i.hasNext()) {
            Entry<String, Reqest> e = i.next();
            if (e == null) continue;
            if (e.getValue().isOvertime()){
                i.remove();
//                if(e.getValue() instanceof ReqestAbs){
//
//                }
            }
        }
        return entMap.isEmpty();
    }
}
