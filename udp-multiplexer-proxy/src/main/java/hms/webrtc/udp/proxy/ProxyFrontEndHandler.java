package hms.webrtc.udp.proxy;

import com.google.common.base.Optional;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * Created by isuru on 3/27/15.
 */
public class ProxyFrontEndHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final ProxyContextCache proxyContextCache;

    public ProxyFrontEndHandler(ProxyContextCache proxyContextCache) {
        this.proxyContextCache = proxyContextCache;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final DatagramPacket msg) throws Exception {

        if(!proxyContextCache.get(ProxyForwardResolver.getKeyForEndpoint(msg)).isPresent()) {
            System.out.println("1. Adding to the proxy cache ...");
            final Channel inboundChannel = ctx.channel();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.
                    group(inboundChannel.eventLoop()).
                    channel(NioDatagramChannel.class).
                    handler(new ProxyBackEndHandler(proxyContextCache));

            bootstrap.bind(0).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        System.out.println("Putting new context to the cache");
                        proxyContextCache.put(ProxyForwardResolver.getKeyForEndpoint(msg), new ProxyContext(future.channel(), msg.sender(), ctx.channel()));
                    }
                }
            });
        }

        
        System.out.println("2. Read inbound data = " + msg);

        final Optional<ProxyContext> proxyContextOptional = proxyContextCache.get(ProxyForwardResolver.getKeyForEndpoint(msg));
        if(proxyContextOptional.isPresent() && proxyContextOptional.get().getOutBindChannel().isActive()) {
            msg.content().retain();
            proxyContextOptional.get().getOutBindChannel().writeAndFlush(new DatagramPacket(msg.content(), new InetSocketAddress("127.0.0.1", 40002))).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        System.out.println("Operation was not success");
                        proxyContextOptional.get().getOutBindChannel().close();
                    } else {
                        System.out.println("Operation was success");
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
