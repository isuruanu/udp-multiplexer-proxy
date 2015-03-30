package hms.webrtc.udp.proxy;

import com.google.common.base.Optional;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
    @Qualifier("proxyBackEndHandler")
    private ChannelHandler proxyBackEndHandler;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final DatagramPacket msg) throws Exception {

        if(!proxyContextCache.get(ProxyForwardResolver.getKeyForEndpoint(msg)).isPresent()) {
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
                        proxyContextCache.put(ProxyForwardResolver.getKeyForEndpoint(msg), new ProxyContext(future.channel(), msg.sender(), ctx.channel()));
                    }
                }
            });
        }

        final Optional<ProxyContext> proxyContextOptional = proxyContextCache.get(ProxyForwardResolver.getKeyForEndpoint(msg));
        if(proxyContextOptional.isPresent() && proxyContextOptional.get().getOutBindChannel().isActive()) {
            msg.content().retain();
            proxyContextOptional.get().getOutBindChannel().writeAndFlush(new DatagramPacket(msg.content(), new InetSocketAddress("127.0.0.1", 40002))).addListener(new ChannelFutureListener() {
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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
