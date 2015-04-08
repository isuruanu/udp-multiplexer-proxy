package hms.webrtc.udp.proxy.remote;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by isuru on 3/30/15.
 */
public class ProxyRemoteControlHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    public static final String remoteConfigurationPattern =
            "^(?<ssrc>[0-9]*):(?<rtpPort>[0-9]*):(?<rtcpPort>[0-9]*):(?<stunUsername>.*:.*)$";

    public static final Pattern pattern = Pattern.compile(ProxyRemoteControlHandler.remoteConfigurationPattern);

    @Autowired
    @Qualifier("remoteControlCache")
    RemoteControlCache remoteControlCache;

    @Autowired
    @Qualifier("dtlsCache")
    DtlsCache dtlsCache;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)  {
        try {
            String configuration = msg.content().toString(CharsetUtil.UTF_8);
            RemoteConfiguration remoteConfiguration = RemoteConfiguration.buildRemoteConfiguration(configuration);

            remoteControlCache.put("rtp:"+remoteConfiguration.getSsrc(), remoteConfiguration.getRtpPort());
            remoteControlCache.put("rtp:"+remoteConfiguration.getStunUsername(), remoteConfiguration.getRtpPort());
            remoteControlCache.put("rtcp:"+remoteConfiguration.getSsrc(), remoteConfiguration.getRtcpPort());
            remoteControlCache.put("rtcp:"+remoteConfiguration.getStunUsername(), remoteConfiguration.getRtcpPort());

            dtlsCache.put(remoteConfiguration.getRtpPort(), remoteConfiguration.getSsrc());
            dtlsCache.put(remoteConfiguration.getRtcpPort(), remoteConfiguration.getSsrc());

            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("SUCCESS", CharsetUtil.UTF_8), msg.sender()));
        } catch (Exception e) {
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("ERROR", CharsetUtil.UTF_8), msg.sender()));
        }
    }

    public static class RemoteConfiguration {
        private final String ssrc;
        private final int rtpPort;
        private final int rtcpPort;
        private final String stunUsername;

        public RemoteConfiguration(String ssrc, int rtpPort, int rtcpPort, String stunUsername) {
            this.ssrc = ssrc;
            this.rtpPort = rtpPort;
            this.rtcpPort = rtcpPort;
            this.stunUsername = stunUsername;
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

        public String getStunUsername() {
            return stunUsername;
        }

        public static final RemoteConfiguration buildRemoteConfiguration(String configuration) throws InvalidRemoteConfigurationException{
            Matcher matcher = pattern.matcher(configuration);
            if(!matcher.matches()) {
                throw new InvalidRemoteConfigurationException();
            }

            return new RemoteConfiguration(matcher.group("ssrc"),
                    Integer.valueOf(matcher.group("rtpPort")),
                    Integer.valueOf(matcher.group("rtcpPort")),
                    matcher.group("stunUsername"));
        }


        public static class InvalidRemoteConfigurationException extends Exception {
        }
    }
}
