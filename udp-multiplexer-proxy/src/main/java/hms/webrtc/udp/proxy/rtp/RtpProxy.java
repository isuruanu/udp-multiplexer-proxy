package hms.webrtc.udp.proxy.rtp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * Created by isuru on 3/29/15.
 */
@Component
public class RtpProxy {

    @Autowired
    @Qualifier("rtpBootStrap")
    Bootstrap rtpBootStrap;

    @Autowired
    @Qualifier("rtpBindAddress")
    InetSocketAddress rtpBindAddress;

    private Channel channel;

    @PostConstruct
    public void start() throws InterruptedException {
        channel = rtpBootStrap.bind(rtpBindAddress).sync().channel().closeFuture().sync().channel();
    }

    @PreDestroy
    public void stop() {
        channel.close();
    }

}
