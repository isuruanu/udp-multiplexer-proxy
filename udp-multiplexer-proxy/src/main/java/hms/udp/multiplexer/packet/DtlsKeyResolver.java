package hms.udp.multiplexer.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by isuru on 4/8/15.
 */
public class DtlsKeyResolver implements KeyResolver {
    @Override
    public String resolve(DatagramPacket packet, ChannelHandlerContext ctx) throws KeyNotResolvedException {
        return null;
    }
}
