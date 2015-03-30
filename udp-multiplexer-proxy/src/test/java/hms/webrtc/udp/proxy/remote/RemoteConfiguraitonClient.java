/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package hms.webrtc.udp.proxy.remote;

import hms.webrtc.udp.proxy.RtpPartyAHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import org.testng.Assert;

import java.net.InetSocketAddress;

public final class RemoteConfiguraitonClient {

    static final int PORT = Integer.parseInt(System.getProperty("port", "35000"));

    private static Channel ch;

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new SimpleChannelInboundHandler<DatagramPacket>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                            if("SUCCESS".equals(msg.content().toString(CharsetUtil.UTF_8))) {
                                System.out.println("Sending configuration is success.");
                            }
                            ctx.close();
                        }
                    });

            ch = b.bind(34001).sync().channel();

            ch.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("78737383:40000:40001", CharsetUtil.UTF_8),
                    new InetSocketAddress("127.0.0.1", PORT))).sync();

            if (!ch.closeFuture().await(5000)) {
                Assert.fail("Rtp communication timeout");
            } else {
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
