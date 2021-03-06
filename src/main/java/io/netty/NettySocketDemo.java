package io.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/5.
 */
public class NettySocketDemo {
    public static final int VERSION = 100;

    public static final  String message = "hello hello hello hello hello ";

    public static void main(String[] args) {
        NettySocketDemo nettySocketDemo = new NettySocketDemo();
        nettySocketDemo.connect("127.0.0.1",8080);
    }

    public  void connect(String host,int port){
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
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
                String sb ="";
                for (int y = 50; y > 0; y-- ) {
                    sb += message;
                }
                byte[] messageBytes = sb.getBytes("utf-8");
                buffer.writeInt(messageBytes.length);
                buffer.writeChar(VERSION);
                buffer.writeBytes(messageBytes);
                ctx.writeAndFlush(buffer);
            }

        }
    }
}
