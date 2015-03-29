package hms.webrtc.udp.proxy.stun;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.mobicents.media.io.stun.StunException;
import org.mobicents.media.io.stun.messages.StunMessage;
import org.mobicents.media.io.stun.messages.StunRequest;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by isuru on 3/29/15.
 */
public class StunHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        System.out.println("Recieved msg " + msg.sender());
        msg.content().retain();
        int readableBytes = msg.content().readableBytes();
        byte[] bytes = new byte[readableBytes];
        msg.content().readBytes(bytes);

        StunMessage stunMessage = null;
        try {
            stunMessage = StunMessage.decode(bytes, (char) 0, (char) bytes.length);
        } catch (StunException e) {
            e.printStackTrace();
        }

        if(stunMessage instanceof StunRequest) {
            byte[] stunResponseBytes = new byte[0];
            try {
                stunResponseBytes = StunRequestProcessor.processRequest((StunRequest) stunMessage, (InetSocketAddress) ctx.channel().localAddress(), msg.sender());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(stunResponseBytes), msg.sender()));
        }

        System.out.println("Receive a stun request ? " + StunMessage.isRequestType(stunMessage.getMessageType()));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
