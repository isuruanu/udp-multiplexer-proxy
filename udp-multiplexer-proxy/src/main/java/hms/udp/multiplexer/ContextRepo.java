package hms.udp.multiplexer;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;

/**
 * Created by isuru on 4/8/15.
 */
public class ContextRepo {

   private final Cache<String, Context> cache;

    public ContextRepo(){
        cache = CacheBuilder.<String, Context>newBuilder().
                maximumSize(1000).
                expireAfterAccess(1, TimeUnit.MINUTES).
                build();
    }

    public Optional<Context> get(String key) {
        return Optional.fromNullable(cache.getIfPresent(key));
    }

    public void save(String key, Context context) {
        cache.put(key, context);
    }

    public static class Context {

        private Channel partyAChannel;
        
        private Channel partyBChannel;
    }
}
