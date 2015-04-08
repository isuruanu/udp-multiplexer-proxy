package hms.udp.multiplexer;

import com.google.common.base.Optional;
import hms.udp.multiplexer.packet.KeyResolver;
import hms.udp.multiplexer.packet.PacketTypeChecker;
import hms.udp.multiplexer.processor.PacketProcessor;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * Created by isuru on 4/8/15.
 */
@ChannelHandler.Sharable
public class InboundHandler extends SimpleChannelInboundHandler<DatagramPacket>{

    private final PacketTypeChecker packetTypeChecker;
    private final ContextRepo contextRepo;
    private final KeyResolver keyResolver;
    private final PacketProcessor packetProcessor;

    public InboundHandler(PacketTypeChecker packetTypeChecker,
                          ContextRepo contextRepo,
                          KeyResolver keyResolver,
                          PacketProcessor packetProcessor) {
        this.packetTypeChecker = packetTypeChecker;
        this.contextRepo = contextRepo;
        this.keyResolver = keyResolver;
        this.packetProcessor = packetProcessor;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        if(!packetTypeChecker.isAPacketOfType(msg)){
            ctx.fireChannelRead(msg);
        }

        try {
            String resolve = keyResolver.resolve(msg, ctx);
            Optional<ContextRepo.Context> relayContext = contextRepo.get(resolve);
            if(relayContext.isPresent()) {
                packetProcessor.processPacket(ctx, msg, relayContext.get());
            }
        } catch (Exception e) {
        }
    }

}
