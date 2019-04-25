package server.contentObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


// hashmap which contains array of V for K
public class ArrayMap<K,V> {
    public Map<K, List<V>> map = new ConcurrentHashMap<>();

    public void add(K key, V value)
    {
        if (!map.containsKey(key))
            map.put(key, new ArrayList<>());
        map.get(key).add(value);
    }

    public void remove(K key, V value)
    {
        if (!map.containsKey(key))
            return;
        map.get(key).remove(value);
        if (map.get(key).size() == 0)
            map.remove(key);
    }

    public List<V> getValues(K key)
    {
        if (map.containsKey(key))
            return map.get(key);
        return null;
    }
}
