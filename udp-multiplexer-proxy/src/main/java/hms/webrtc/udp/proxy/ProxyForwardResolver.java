package hms.webrtc.udp.proxy;

import io.netty.channel.socket.DatagramPacket;

/**
 * Created by isuru on 3/27/15.
 */
public class ProxyForwardResolver {
    public static String getKeyForEndpoint(DatagramPacket packet) {
        return "127.0.0.1:40002";
    }
}
