package hms.webrtc.udp.proxy.remote;

import hms.webrtc.udp.proxy.ProxyContextCache;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Created by isuru on 3/30/15.
 */
@Component
public class ProxyRemoteControl {

    @Autowired
    @Qualifier("remoteControlBootStrap")
    Bootstrap remoteControlBootStrap;

    @Autowired
    @Qualifier("remoteControlBindAddress")
    InetSocketAddress remoteControlBindAddress;

    private Channel channel;

    @PostConstruct
    public void start() throws InterruptedException {
        channel = remoteControlBootStrap.bind(remoteControlBindAddress).sync().channel();
    }

    @PreDestroy
    public void stop() {
        channel.close();
    }

}
