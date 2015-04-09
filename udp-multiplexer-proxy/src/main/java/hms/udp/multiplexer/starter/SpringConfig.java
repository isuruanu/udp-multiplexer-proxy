package hms.udp.multiplexer.starter;

import hms.udp.multiplexer.ContextRepo;
import hms.udp.multiplexer.InboundHandler;
import hms.udp.multiplexer.packet.*;
import hms.udp.multiplexer.processor.PacketProcessor;
import hms.udp.multiplexer.processor.RtpPacketProcessor;
import hms.udp.multiplexer.processor.StunPacketProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("hms.udp.multiplexer")
@PropertySource("classpath:multiplexer.properties")
public class SpringConfig {

    @Value("${default.nio.worker.thread.count}")
    private int defaultNioWorkerThreadCount;

    @Value("${inbound.channel.one.port}")
    private int inboundChannel1Port;

    @Value("${inbound.channel.two.port}")
    private int inboundChannel2Port;

    @Value("${proxy.remote.control.port}")
    private int proxyRemoteControlPort;

    @Value("${default.bind.host.interface.ip}")
    private String defaultBindHostInterfaceIp;

    @Value("${rtp.engine.host.ip}")
    private String rtpEngineIp;

    /*
    * Rtp packet handling
    * */

    @Bean(name = "rtpPacketTypeChecker")
    public PacketTypeChecker rtpPacketTypeChecker(){
        return new RtpPacketTypeChecker();
    }

    private final ContextRepo rtpContextRepo = new ContextRepo();

    @Bean(name = "rtpContextRepo")
    public ContextRepo rtpContextRepo(){
        return rtpContextRepo;
    }

    @Bean(name = "rtpKeyResolver")
    public KeyResolver rtpKeyResolver(){
        return new RtpKeyResolver();
    }

    @Bean(name = "rtpPacketProcessor")
    public PacketProcessor rtpPacketProcessor(){
        return new RtpPacketProcessor();
    }

    @Bean(name = "rtpHandler")
    public ChannelHandler rtpChannelHandler() {
        return new InboundHandler(rtpPacketTypeChecker(),
                                    rtpContextRepo(),
                                    rtpKeyResolver(),
                                    rtpPacketProcessor());
    }

    @Bean(name = "channel1NioEventLoopGroup")
    public NioEventLoopGroup channel1NioEventLoopGroup(){
        return new NioEventLoopGroup(defaultNioWorkerThreadCount);
    }

    @Bean(name = "channel1InitializationHandler")
    public ChannelHandler channel1InitializerHandler(){
        return new ChannelInitializer<NioDatagramChannel>() {
            @Override
            protected void initChannel(NioDatagramChannel ch) throws Exception {
                ch.pipeline().addLast(rtpChannelHandler()).addLast(stunChannelHandler());
            }
        };
    }


        /*
    * Stun packet handling
    * */

    @Bean(name = "stunPacketTypeChecker")
    public PacketTypeChecker stunPacketTypeChecker(){
        return new StunPacketTypeChecker();
    }

    private final ContextRepo stunContextRepo = new ContextRepo();

    @Bean(name = "stunContextRepo")
    public ContextRepo stunContextRepo(){
        return stunContextRepo;
    }

    @Bean(name = "stunKeyResolver")
    public KeyResolver stunKeyResolver(){
        return new StunKeyResolver();
    }

    @Bean(name = "stunPacketProcessor")
    public PacketProcessor stunPacketProcessor(){
        return new StunPacketProcessor();
    }

    @Bean(name = "stunHandler")
    public ChannelHandler stunChannelHandler() {
        return new InboundHandler(stunPacketTypeChecker(),
                stunContextRepo(),
                stunKeyResolver(),
                stunPacketProcessor());
    }




    @Bean(name = "channel1Bootstrap")
    public Bootstrap channel1Bootstrap(){
        return new Bootstrap().
                group(channel1NioEventLoopGroup()).
                channel(NioDatagramChannel.class).
                handler(channel1InitializerHandler());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
