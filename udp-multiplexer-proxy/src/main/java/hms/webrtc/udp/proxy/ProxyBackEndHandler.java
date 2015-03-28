package hms.webrtc.udp.proxy;

import com.google.common.base.Optional;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * Created by isuru on 3/27/15.
 */
public class ProxyBackEndHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private ProxyContextCache cache;

    public ProxyBackEndHandler(ProxyContextCache cache) {

        this.cache = cache;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            ctx.channel().writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        System.out.println("3. Receive data from backend " + msg.content().toString(CharsetUtil.UTF_8));
        Optional<ProxyContext> proxyContextOptional = cache.get(ProxyForwardResolver.getKeyForEndpoint(msg));
        if(proxyContextOptional.isPresent()) {
            ProxyContext proxyContext = proxyContextOptional.get();
            System.out.println(proxyContext.getInboundChannel());
            System.out.println(proxyContext.getSender());
            msg.content().retain();
            proxyContext.getInboundChannel().writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(msg.content()), proxyContext.getSender()));
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