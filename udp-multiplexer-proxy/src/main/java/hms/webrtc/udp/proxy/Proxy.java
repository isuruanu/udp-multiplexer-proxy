package hms.webrtc.udp.proxy;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class Proxy {
    public static void main(String[] args) throws InterruptedException {

        ProxyContextCache<String, ProxyContext> proxyContextCache = new ProxyContextCache<String, ProxyContext>(new RemovalListener<String, ProxyContext>() {
            @Override
            public void onRemoval(RemovalNotification<String, ProxyContext> removalNotification) {
                removalNotification.getValue().getOutBindChannel().close();
            }
        });

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
            bootstrap.
                group(eventExecutors).
                channel(NioDatagramChannel.class).
                handler(new ProxyFrontEndHandler());

        bootstrap.bind(40000).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()) {
                    //TODO on success
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
