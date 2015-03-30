package hms.webrtc.udp.proxy;

import com.google.common.base.Optional;
import hms.webrtc.udp.proxy.remote.RemoteControlCache;
import hms.webrtc.udp.proxy.stun.StunRequestProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.mobicents.media.io.stun.StunException;
import org.mobicents.media.io.stun.messages.StunMessage;
import org.mobicents.media.io.stun.messages.StunRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by isuru on 3/27/15.
 */
@ChannelHandler.Sharable
public class ProxyFrontEndHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Autowired
    @Qualifier("proxyContextCache")
    private ProxyContextCache proxyContextCache;

    @Autowired
    @Qualifier("remoteControlCache")
    RemoteControlCache remoteControlCache;

    private ChannelHandler proxyBackEndHandler;

    private ProxyKeyResolver proxyKeyResolver;

    @Value("${rtp.engine.host.ip}")
    private String rtpEngineIpAddress;

    public ProxyFrontEndHandler(ChannelHandler proxyBackEndHandler, ProxyKeyResolver proxyKeyResolver) {
        this.proxyBackEndHandler = proxyBackEndHandler;
        this.proxyKeyResolver = proxyKeyResolver;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final DatagramPacket msg) throws Exception {

        /* TODO: Handle stun separately*/
        msg.content().retain();
        int readableBytes = msg.content().readableBytes();
        byte[] bytes = new byte[readableBytes];
        msg.content().readBytes(bytes);

        msg.content().resetReaderIndex();

        StunMessage stunMessage = null;
        try {
            stunMessage = StunMessage.decode(bytes, (char) 0, (char) bytes.length);
            if(stunMessage instanceof StunRequest) {
                byte[] stunResponseBytes = new byte[0];
                try {
                    stunResponseBytes = StunRequestProcessor.processRequest((StunRequest) stunMessage, (InetSocketAddress) ctx.channel().localAddress(), msg.sender());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(stunResponseBytes), msg.sender()));
                return;
            }
        } catch (StunException e) {
        }

        final String keyForEndpoint = proxyKeyResolver.getKeyForEndpoint(msg);

        if(!proxyContextCache.get(keyForEndpoint).isPresent()) {
            final Channel inboundChannel = ctx.channel();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.
                    group(inboundChannel.eventLoop()).
                    channel(NioDatagramChannel.class).
                    handler(proxyBackEndHandler);

            bootstrap.bind(0).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        proxyContextCache.put(keyForEndpoint, new ProxyContext(future.channel(), new InetSocketAddress(rtpEngineIpAddress, remoteControlCache.get(keyForEndpoint)), msg.sender(), ctx.channel()));
                    }
                }
            });
        }

        final Optional<ProxyContext> proxyContextOptional = proxyContextCache.get(keyForEndpoint);
        if(proxyContextOptional.isPresent() && proxyContextOptional.get().getOutBindChannel().isActive()) {
            msg.content().retain();
            proxyContextOptional.get().getOutBindChannel().writeAndFlush(new DatagramPacket(msg.content(), proxyContextOptional.get().getReceiver())).
                    addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                proxyContextOptional.get().getOutBindChannel().close();
                            }
                        }
                    });
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
