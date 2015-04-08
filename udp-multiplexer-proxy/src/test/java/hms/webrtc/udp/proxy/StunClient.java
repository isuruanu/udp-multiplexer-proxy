package hms.webrtc.udp.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.mobicents.media.io.stun.StunException;
import org.mobicents.media.io.stun.messages.StunMessage;
import org.mobicents.media.io.stun.messages.StunRequest;
import org.mobicents.media.io.stun.messages.attributes.general.PriorityAttribute;
import org.mobicents.media.io.stun.messages.attributes.general.UsernameAttribute;
import org.testng.Assert;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

/**
 * Created by isuru on 3/29/15.
 */
public class StunClient {


    static final int PORT = Integer.parseInt(System.getProperty("port", "40100"));

    private static Channel ch;


    public static void main(String[] args) throws UnsupportedEncodingException {
        StunRequest stunRequest = new StunRequest();
        stunRequest.setMessageType(StunMessage.BINDING_REQUEST);
        byte tranID[] = new byte[StunMessage.TRANSACTION_ID_LENGTH];

        UsernameAttribute usernameAttr = new UsernameAttribute();
        usernameAttr.setUsername("remote:local".getBytes("UTF-8"));

        PriorityAttribute priorityAttribute = new PriorityAttribute();
        priorityAttribute.setPriority(1);


        try {
            stunRequest.setTransactionID(tranID);
            stunRequest.addAttribute(usernameAttr);
            stunRequest.addAttribute(priorityAttribute);
        } catch (StunException e) {
            e.printStackTrace();
        }
        byte[] encode = stunRequest.encode();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new StunClientHandler());

            ch = b.bind(36001).sync().channel();

            ch.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer(encode),
                    new InetSocketAddress("127.0.0.1", PORT))).sync();

            if (!ch.closeFuture().await(5000)) {
                Assert.fail("Stun communication timeout");
            } else {
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }
}
