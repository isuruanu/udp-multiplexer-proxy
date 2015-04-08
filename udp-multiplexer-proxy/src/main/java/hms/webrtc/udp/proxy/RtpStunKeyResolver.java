package hms.webrtc.udp.proxy;

import io.netty.channel.socket.DatagramPacket;

import org.mobicents.media.io.stun.StunException;
import org.mobicents.media.io.stun.messages.StunMessage;
import org.mobicents.media.io.stun.messages.attributes.StunAttribute;
import org.mobicents.media.io.stun.messages.attributes.general.UsernameAttribute;

import java.io.UnsupportedEncodingException;

/**
 * Created by isuru on 3/31/15.
 */
public class RtpStunKeyResolver implements StunKeyResolver {

    @Override
    public String getKeyForEndpoint(DatagramPacket msg) throws StunException, UnsupportedEncodingException {

        /* TODO: Handle stun separately*/
        msg.content().retain();
        int readableBytes = msg.content().readableBytes();
        byte[] bytes = new byte[readableBytes];
        msg.content().readBytes(bytes);
        msg.content().resetReaderIndex();
        StunMessage stunMessage = StunMessage.decode(bytes, (char) 0, (char) bytes.length);
        UsernameAttribute attribute = (UsernameAttribute)stunMessage.getAttribute(StunAttribute.USERNAME);

        return  "rtp:" + new String(attribute.getUsername(), "UTF-8");
    }
}
