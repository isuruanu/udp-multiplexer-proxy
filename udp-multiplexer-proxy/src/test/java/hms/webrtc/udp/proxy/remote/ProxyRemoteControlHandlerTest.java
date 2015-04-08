package hms.webrtc.udp.proxy.remote;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyRemoteControlHandlerTest {
    @Test
    public void testRemoteConfigurationPattern() {
        Pattern pattern = Pattern.compile(ProxyRemoteControlHandler.remoteConfigurationPattern);
        Matcher matcher = pattern.matcher("78737383:40000:40001:uoiuoiuoi:kjkajdlkf");
        Assert.assertTrue(matcher.matches());
        Assert.assertEquals(matcher.group("ssrc"), "78737383");
        Assert.assertEquals(matcher.group("rtpPort"), "40000");
        Assert.assertEquals(matcher.group("rtcpPort"), "40001");
        Assert.assertEquals(matcher.group("stunUsername"), "uoiuoiuoi:kjkajdlkf");
    }
}