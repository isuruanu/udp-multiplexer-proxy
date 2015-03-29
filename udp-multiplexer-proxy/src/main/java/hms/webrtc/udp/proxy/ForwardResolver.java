package hms.webrtc.udp.proxy;

import io.netty.channel.socket.DatagramPacket;
import org.mobicents.media.server.impl.rtp.RtpPacket;

/**
 * Created by isuru on 3/27/15.
 */
public class ForwardResolver {

    public static String getKeyForEndpoint(DatagramPacket packet) {
        packet.content().retain();
        int readableBytes = packet.content().readableBytes();
        RtpPacket rtpPacket = new RtpPacket(readableBytes, true);
        byte[] content = new byte[readableBytes];
        packet.content().readBytes(content);
        rtpPacket.getBuffer().put(content);
        packet.content().resetReaderIndex();
        System.out.println("Synchronized source - " + rtpPacket.getSyncSource());
        return String.valueOf(rtpPacket.getSyncSource());
    }
}
