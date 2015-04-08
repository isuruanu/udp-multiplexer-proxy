package hms.udp.multiplexer.packet;

import io.netty.channel.socket.DatagramPacket;

/**
 * Created by isuru on 4/8/15.
 */
public interface PacketTypeChecker {
    boolean isAPacketOfType(DatagramPacket msg);
}
