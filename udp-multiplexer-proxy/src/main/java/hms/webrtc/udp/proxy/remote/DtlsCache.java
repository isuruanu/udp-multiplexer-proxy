package hms.webrtc.udp.proxy.remote;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Created by isuru on 4/2/15.
 */
public class DtlsCache {
    private Cache<Integer, String> cache;

    public DtlsCache() {
        cache = CacheBuilder.<Integer, String>newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES).build();
    }

    public void put(Integer key, String value) {
        cache.put(key, value);
    }

    public String get(Integer key) {
        return cache.getIfPresent(key);
    }
}
