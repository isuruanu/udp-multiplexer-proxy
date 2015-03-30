package hms.webrtc.udp.proxy.starter;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import hms.webrtc.udp.proxy.ProxyBackEndHandler;
import hms.webrtc.udp.proxy.ProxyContext;
import hms.webrtc.udp.proxy.ProxyContextCache;
import hms.webrtc.udp.proxy.ProxyFrontEndHandler;
import hms.webrtc.udp.proxy.remote.ProxyRemoteControlHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.InetSocketAddress;

@Configuration
@ComponentScan("hms.webrtc.udp.proxy")
@PropertySource("classpath:multiplexer.properties")
public class SpringConfig {

    @Value("${default.nio.worker.thread.count}")
    private int defaultNioWorkerThreadCount;

    @Value("${rtp.inbound.port}")
    private int rtpInboundPort;

    @Value("${rtcp.inbound.port}")
    private int rtcpInboundPort;

    @Value("${proxy.remote.control.port}")
    private int proxyRemoteControlPort;

    @Value("${default.bind.host.interface.ip}")
    private String defaultBindHostInterfaceIp;

    @Bean(name = "remoteControlNioEventLoopGroup")
    public NioEventLoopGroup remoteControlNioEventLoopGroup(){
        return new NioEventLoopGroup(defaultNioWorkerThreadCount);
    }

    @Bean(name = "rtpNioEventLoopGroup")
    public NioEventLoopGroup rtpNioEventLoopGroup(){
        return new NioEventLoopGroup(defaultNioWorkerThreadCount);
    }


    @Bean(name = "rtcpNioEventLoopGroup")
    public NioEventLoopGroup rtcpNioEventLoopGroup(){
        return new NioEventLoopGroup(defaultNioWorkerThreadCount);
    }

    @Bean(name = "proxyContextCache")
    public ProxyContextCache proxyContextCache() {
        return new ProxyContextCache<String, ProxyContext>(new RemovalListener<String, ProxyContext>() {
            @Override
            public void onRemoval(RemovalNotification<String, ProxyContext> removalNotification) {
                removalNotification.getValue().getOutBindChannel().close();
            }
        });
    }

    @Bean(name = "rtpBindAddress")
    public InetSocketAddress rtpBindAddress() {
        return new InetSocketAddress(defaultBindHostInterfaceIp, rtpInboundPort);
    }

    @Bean(name = "rtcpBindAddress")
    public InetSocketAddress rtcpBindAddress() {
        return new InetSocketAddress(defaultBindHostInterfaceIp, rtcpInboundPort);
    }

    @Bean(name = "remoteControlBindAddress")
    public InetSocketAddress remoteControlBindAddress() {
        return new InetSocketAddress(defaultBindHostInterfaceIp, proxyRemoteControlPort);
    }

    @Bean(name = "proxyFrontEndHandler")
    public ChannelHandler proxyFrontEndHandler() {
        return new ProxyFrontEndHandler();
    }

    @Bean(name = "proxyBackEndHandler")
    public ChannelHandler proxyBackChannelHandler() {
        return new ProxyBackEndHandler();
    }

    @Bean(name = "proxyRemoteControlHandler")
    public ChannelHandler proxyRemoteControlHandler() {
        return new ProxyRemoteControlHandler();
    }

    @Bean(name = "rtpBootStrap")
    public Bootstrap rtpBootStrap(){
        return new Bootstrap().
                group(rtpNioEventLoopGroup()).
                channel(NioDatagramChannel.class).
                handler(proxyFrontEndHandler());
    }

    @Bean(name = "remoteControlBootStrap")
    public Bootstrap remoteControlBootStrap(){
        return new Bootstrap().
                group(remoteControlNioEventLoopGroup()).
                channel(NioDatagramChannel.class).
                handler(proxyRemoteControlHandler());
    }

    @Bean(name = "rtcpBootStrap")
    public Bootstrap rtcpBootStrap(){
        return new Bootstrap().
                group(rtcpNioEventLoopGroup()).
                channel(NioDatagramChannel.class).
                handler(proxyFrontEndHandler());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
