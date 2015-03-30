package hms.webrtc.udp.proxy.rtp;

import hms.webrtc.udp.proxy.ProxyKeyResolver;
import io.netty.channel.socket.DatagramPacket;
import org.mobicents.media.server.impl.rtp.RtpPacket;

/**
 * Created by isuru on 3/30/15.
 */
public class RtpKeyResolver implements ProxyKeyResolver {

    @Override
    public String getKeyForEndpoint(DatagramPacket packet) {
        packet.content().retain();

        int readableBytes = packet.content().readableBytes();
        byte[] content = new byte[readableBytes];
        packet.content().readBytes(content);

        RtpPacket rtpPacket = new RtpPacket(readableBytes, true);
        rtpPacket.getBuffer().put(content);

        packet.content().resetReaderIndex();

        return "rtp:"+String.valueOf(rtpPacket.getSyncSource());
    }
}
