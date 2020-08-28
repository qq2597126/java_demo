package io.netty.decoder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/28.
 */
public class JsonChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //数据读取
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,0,4,0,4));
        ch.pipeline().addLast(new StringDecoder(Charset.forName("utf-8")));
        ch.pipeline().addLast(new JsonDecoder());
    }
}
