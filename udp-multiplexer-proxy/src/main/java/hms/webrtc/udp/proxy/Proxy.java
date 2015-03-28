package hms.webrtc.udp.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Created by isuru on 3/27/15.
 */
public class Proxy {
    public static void main(String[] args) throws InterruptedException {
        ProxyContextCache proxyContextCache = new ProxyContextCache();
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
            bootstrap.
                group(eventExecutors).
                channel(NioDatagramChannel.class).
                handler(new ProxyFrontEndHandler(proxyContextCache));

        bootstrap.bind(40000).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    System.out.println("Proxy initialization is success...");
                }
            }
        }).sync().channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.printf("server shutting down");
            }
        }).sync();

        eventExecutors.shutdownGracefully();
    }
}
