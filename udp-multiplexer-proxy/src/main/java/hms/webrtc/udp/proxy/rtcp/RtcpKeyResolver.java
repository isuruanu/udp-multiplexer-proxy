package hms.webrtc.udp.proxy.rtcp;

import hms.webrtc.udp.proxy.ProxyKeyResolver;
import io.netty.channel.socket.DatagramPacket;
import org.mobicents.media.server.impl.rtcp.RtcpPacket;
import org.mobicents.media.server.impl.rtp.RtpPacket;

/**
 * Created by isuru on 3/30/15.
 */
public class RtcpKeyResolver implements ProxyKeyResolver {

    @Override
    public String getKeyForEndpoint(DatagramPacket packet) {
        packet.content().retain();

        int readableBytes = packet.content().readableBytes();
        byte[] content = new byte[readableBytes];
        packet.content().readBytes(content);

        RtcpPacket rtcpPacket = new RtcpPacket();
        rtcpPacket.decode(content, 0);

        packet.content().resetReaderIndex();

        return "rtcp:"+String.valueOf(rtcpPacket.getReport().getSsrc());
    }
}
