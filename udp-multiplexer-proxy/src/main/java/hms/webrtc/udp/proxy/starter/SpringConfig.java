package hms.webrtc.udp.proxy.starter;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import hms.webrtc.udp.proxy.*;
import hms.webrtc.udp.proxy.remote.DtlsCache;
import hms.webrtc.udp.proxy.remote.ProxyRemoteControlHandler;
import hms.webrtc.udp.proxy.remote.RemoteControlCache;
import hms.webrtc.udp.proxy.rtcp.RtcpKeyResolver;
import hms.webrtc.udp.proxy.rtp.RtpKeyResolver;
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

    @Bean(name = "rtpFrontEndHandler")
    public ChannelHandler rtpFrontEndHandler() {
        return new ProxyFrontEndHandler(rtpBackEndHandler(), rtpKeyResolver(), rtpStunKeyResolver());
    }

    @Bean(name = "rtpBackEndHandler")
    public ChannelHandler rtpBackEndHandler() {
        return new ProxyBackEndHandler(rtpKeyResolver());
    }

    @Bean(name = "rtcpFrontEndHandler")
    public ChannelHandler rtcpFrontEndHandler() {
        return new ProxyFrontEndHandler(rtcpBackEndHandler(), rtcpKeyResolver(), rtcpStunKeyResolver());
    }

    @Bean(name = "rtcpBackEndHandler")
    public ChannelHandler rtcpBackEndHandler() {
        return new ProxyBackEndHandler(rtcpKeyResolver());
    }

    @Bean(name = "rtcpStunKeyResolver")
    public RtcpStunKeyResolver rtcpStunKeyResolver() {
        return new RtcpStunKeyResolver();
    }

    @Bean(name = "rtpStunKeyResolver")
    public RtpStunKeyResolver rtpStunKeyResolver() {
        return new RtpStunKeyResolver();
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
                handler(rtpFrontEndHandler());
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
                handler(rtcpFrontEndHandler());
    }

    @Bean(name = "rtcpKeyResolver")
    public RtcpKeyResolver rtcpKeyResolver(){
        return new RtcpKeyResolver();
    }

    @Bean(name = "rtpKeyResolver")
    public RtpKeyResolver rtpKeyResolver(){
        return new RtpKeyResolver();
    }

    @Bean(name = "remoteControlCache")
    public RemoteControlCache remoteControlCache() {
        return new RemoteControlCache();
    }

    @Bean(name = "dtlsCache")
    public DtlsCache dtlsCache() {
        return new DtlsCache();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
