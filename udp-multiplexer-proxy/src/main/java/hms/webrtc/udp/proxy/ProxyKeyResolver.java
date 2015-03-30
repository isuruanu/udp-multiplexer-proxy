package hms.webrtc.udp.proxy;

import io.netty.channel.socket.DatagramPacket;
import org.mobicents.media.server.impl.rtp.RtpPacket;

/**
 * Created by isuru on 3/27/15.
 */
public interface ProxyKeyResolver {

    String getKeyForEndpoint(DatagramPacket packet);
}
