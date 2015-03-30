package hms.webrtc.udp.proxy.remote;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by isuru on 3/30/15.
 */
public class ProxyRemoteControlHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    public static final String remoteConfigurationPattern = "^(?<ssrc>[0-9]*):(?<rtpPort>[0-9]*):(?<rtcpPort>[0-9]*)$";

    public static final Pattern pattern = Pattern.compile(ProxyRemoteControlHandler.remoteConfigurationPattern);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)  {
        try {
            String configuration = msg.content().toString(CharsetUtil.UTF_8);
            RemoteConfiguration remoteConfiguration = RemoteConfiguration.buildRemoteConfiguration(configuration);

            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("SUCCESS", CharsetUtil.UTF_8), msg.sender()));
        } catch (Exception e) {
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("ERROR", CharsetUtil.UTF_8), msg.sender()));
        }
    }

    public static class RemoteConfiguration {
        private final String ssrc;
        private final int rtpPort;
        private final int rtcpPort;

        public RemoteConfiguration(String ssrc, int rtpPort, int rtcpPort) {
            this.ssrc = ssrc;
            this.rtpPort = rtpPort;
            this.rtcpPort = rtcpPort;
        }

        public String getSsrc() {
            return ssrc;
        }

        public int getRtpPort() {
            return rtpPort;
        }

        public int getRtcpPort() {
            return rtcpPort;
        }

        public static final RemoteConfiguration buildRemoteConfiguration(String configuration) throws InvalidRemoteConfigurationException{
            Matcher matcher = pattern.matcher(configuration);
            if(!matcher.matches()) {
                throw new InvalidRemoteConfigurationException();
            }

            return new RemoteConfiguration(matcher.group("ssrc"),
                    Integer.valueOf(matcher.group("rtpPort")),
                    Integer.valueOf(matcher.group("rtcpPort")));
        }


        public static class InvalidRemoteConfigurationException extends Exception {
        }
    }
}
