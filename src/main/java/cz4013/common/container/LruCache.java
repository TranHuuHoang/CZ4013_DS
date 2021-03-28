package cz4013.common.container;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class LruCache<K, V> {
  private Map<K, V> cache;

  public LruCache(int capacity) {
    cache = Collections.synchronizedMap(new LinkedHashMap<K, V>(capacity, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
      }
    });
  }

  public Optional<V> get(K key) {
    return Optional.ofNullable(cache.get(key));
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }
}
