package io.netty.decoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.nio.charset.Charset;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/28.
 */
public class ChannelInitHandler extends ChannelInitializer<SocketChannel> {
    public static final LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder = null;

    public static final EchoServerHandler echoServerHandler=  new EchoServerHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 2, 6));
        ch.pipeline().addLast(new StringDecoder(Charset.forName("utf-8")));
        ch.pipeline().addLast(echoServerHandler);
    }
}
