package hms.webrtc.udp.proxy.remote;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by isuru on 3/30/15.
 */
public class RemoteControlCache {

    private Cache<String, Integer> cache;

    public RemoteControlCache() {
        cache = CacheBuilder.<String, Integer>newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES).build();
    }

    public void put(String key, Integer value) {
        cache.put(key, value);
    }

    public Integer get(String key) {
        return cache.getIfPresent(key);
    }
}
