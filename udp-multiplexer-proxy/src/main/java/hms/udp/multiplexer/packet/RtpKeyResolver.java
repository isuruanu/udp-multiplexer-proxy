package hms.udp.multiplexer.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.mobicents.media.server.impl.rtcp.RtcpPacket;

/**
 * Created by isuru on 4/8/15.
 */
public class RtpKeyResolver implements KeyResolver {
    @Override
    public String resolve(DatagramPacket packet, ChannelHandlerContext ctx) {
        packet.content().retain();

        int readableBytes = packet.content().readableBytes();
        byte[] content = new byte[readableBytes];
        packet.content().readBytes(content);

        RtcpPacket rtcpPacket = new RtcpPacket();
        rtcpPacket.decode(content, 0);

        packet.content().resetReaderIndex();

        return String.valueOf(rtcpPacket.getReport().getSsrc());
    }
}
