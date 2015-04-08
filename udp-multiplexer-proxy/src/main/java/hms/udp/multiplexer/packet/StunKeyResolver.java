package hms.udp.multiplexer.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.mobicents.media.io.stun.StunException;
import org.mobicents.media.io.stun.messages.StunMessage;
import org.mobicents.media.io.stun.messages.attributes.StunAttribute;
import org.mobicents.media.io.stun.messages.attributes.general.UsernameAttribute;

import java.io.UnsupportedEncodingException;

/**
 * Created by isuru on 4/8/15.
 */
public class StunKeyResolver implements KeyResolver {
    @Override
    public String resolve(DatagramPacket packet, ChannelHandlerContext ctx) throws KeyNotResolvedException {
        try {
            packet.content().retain();
            int readableBytes = packet.content().readableBytes();
            byte[] bytes = new byte[readableBytes];
            packet.content().readBytes(bytes);
            packet.content().resetReaderIndex();
            StunMessage stunMessage = StunMessage.decode(bytes, (char) 0, (char) bytes.length);
            UsernameAttribute attribute = (UsernameAttribute)stunMessage.getAttribute(StunAttribute.USERNAME);

            return new String(attribute.getUsername(), "UTF-8");
        } catch (StunException | UnsupportedEncodingException e) {
            throw new KeyNotResolvedException();
        }
    }
}
