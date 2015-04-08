package hms.webrtc.udp.proxy.handlers;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * Created by isuru on 4/2/15.
 */
public class TestClient {
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup()).
                channel(NioDatagramChannel.class).handler(new SimpleChannelInboundHandler<DatagramPacket>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                System.out.println("Datagram packet " + msg.content().toString(CharsetUtil.UTF_8));
            }
        });
        Channel channel = bootstrap.bind(0).channel();
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("Client channel closed.");
                future.channel().close();
            }
        });
        channel.writeAndFlush(new DatagramPacket(
                Unpooled.copiedBuffer("Hello said client", CharsetUtil.UTF_8),
                new InetSocketAddress("127.0.0.1", 12001)));

    }
}
