package hms.webrtc.udp.proxy;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * Created by isuru on 3/28/15.
 */
public class ProxyContext {

    private final Channel outBindChannel;

    private final InetSocketAddress receiver;

    private final InetSocketAddress sender;

    private final Channel inboundChannel;

    public ProxyContext(Channel outBindChannel, InetSocketAddress receiver, InetSocketAddress sender, Channel inboundChannel) {
        this.outBindChannel = outBindChannel;
        this.receiver = receiver;
        this.sender = sender;
        this.inboundChannel = inboundChannel;
    }

    public Channel getOutBindChannel() {
        return outBindChannel;
    }

    public InetSocketAddress getSender() {
        return sender;
    }

    public Channel getInboundChannel() {
        return inboundChannel;
    }

    public InetSocketAddress getReceiver() {
        return receiver;
    }
}
