package hms.udp.multiplexer.packet;

import io.netty.channel.socket.DatagramPacket;

/**
 * Created by isuru on 4/8/15.
 */
public class DtlsPacketTypeChecker implements PacketTypeChecker {
    @Override
    public boolean isAPacketOfType(DatagramPacket msg) {
        byte value = msg.content().readByte();
        msg.content().resetReaderIndex();
        return (value>=20)  && (value <=64);
    }
}
