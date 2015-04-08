package hms.webrtc.udp.proxy;

import io.netty.channel.socket.DatagramPacket;
import org.mobicents.media.io.stun.StunException;

import java.io.UnsupportedEncodingException;

/**
 * Created by isuru on 3/31/15.
 */
public interface StunKeyResolver {
    String getKeyForEndpoint(DatagramPacket packet) throws StunException, UnsupportedEncodingException;
}
