package hms.udp.multiplexer.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by isuru on 4/8/15.
 */
public interface KeyResolver {
    String resolve(DatagramPacket packet, ChannelHandlerContext ctx) throws KeyNotResolvedException;

    public static class KeyNotResolvedException extends Exception {
        public KeyNotResolvedException() {
            super("Packet key could not be resolved.");
        }
    }
}
