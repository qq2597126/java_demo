package io.netty.decoder;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.Charset;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/28.
 */
public class JsonClient {
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    public  void connect(String host,int port){

        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                        socketChannel.pipeline().addLast(new StringEncoder(Charset.forName("utf-8")));
                        socketChannel.pipeline().addLast(new SendClientHandler());
                    }
                });
        try {
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            sync.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("连接成功");
                    }else{
                        System.out.println("连接失败");
                    }
                }
            });
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
    class SendClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf =  (ByteBuf)msg;
            System.out.println("接收到服务器端的数据："+new String(ByteBufUtil.getBytes(buf),"utf-8"));
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("连接成功：");
            for (int x = 5000; x > 0; x-- ) {
                ByteBuf buffer = ctx.alloc().buffer();
                JsonMessageBo jsonMessageBo = new JsonMessageBo(x, "我是消息:" + x, true);
                byte[] messageBytes = JSON.toJSONString(jsonMessageBo).getBytes("utf-8");
                buffer.writeBytes(messageBytes);
                ctx.writeAndFlush(buffer);
            }

        }
    }

    public static void main(String[] args) {
        new JsonClient().connect("127.0.0.1",8080);
    }
}
