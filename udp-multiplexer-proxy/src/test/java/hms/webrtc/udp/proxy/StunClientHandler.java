package hms.webrtc.udp.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.mobicents.media.io.stun.StunException;
import org.mobicents.media.io.stun.messages.StunMessage;
import org.mobicents.media.io.stun.messages.attributes.StunAttribute;
import org.mobicents.media.io.stun.messages.attributes.general.UsernameAttribute;

/**
 * Created by isuru on 3/29/15.
 */
public class StunClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        try {
            msg.content().retain();
            int readableBytes = msg.content().readableBytes();
            byte[] bytes = new byte[readableBytes];
            msg.content().readBytes(bytes);

            StunMessage stunMessage = StunMessage.decode(bytes, (char) 0, (char) bytes.length);

            System.out.println("Stun message type - "  + stunMessage.getClass());
        } catch (StunException e) {
            e.printStackTrace();
        }

        ctx.close();
    }
}
