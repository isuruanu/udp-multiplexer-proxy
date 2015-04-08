package hms.udp.multiplexer.processor;

import hms.udp.multiplexer.ContextRepo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by isuru on 4/8/15.
 */
public class DtlsPacketProcessor implements PacketProcessor {
    @Override
    public void processPacket(ChannelHandlerContext ctx, DatagramPacket packet, ContextRepo.Context context) {

    }
}
