package hms.webrtc.udp.proxy;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import java.util.concurrent.TimeUnit;

/**
 * Created by isuru on 3/27/15.
 */
public class ProxyContextCache<K, V> {

    private final Cache<K, V> proxyContextCache;

    private final RemovalListener<K, V> removalListener;

    public ProxyContextCache(RemovalListener<K, V> removalListener) {
        this.removalListener = removalListener;

        this.proxyContextCache = CacheBuilder.<K, V>newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .removalListener(this.removalListener).build();
    }

    public void put(K key, V value) {
        proxyContextCache.put(key, value);
    }

    public Optional<V> get(K key) {
        return Optional.fromNullable(proxyContextCache.getIfPresent(key));
    }

}
