package hms.webrtc.udp.proxy;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;

/**
 * Created by isuru on 3/27/15.
 */
public class ProxyContextCache {

    private final Cache<String, ProxyContext> proxyContextCache;

    public ProxyContextCache() {
        this.proxyContextCache = CacheBuilder.<String, ProxyContext>newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .removalListener(new RemovalListener<String, ProxyContext>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, ProxyContext> notification) {
                        if(notification.getValue() != null){
                            notification.getValue().getOutBindChannel().close();
                        }
                    }
                }).build();
    }

    public void put(String key, ProxyContext value) {
        proxyContextCache.put(key, value);
    }

    public Optional<ProxyContext> get(String key) {
        return Optional.fromNullable(proxyContextCache.getIfPresent(key));
    }

}
