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
            System.out.println("请输入数据：");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes()));
        }
    }
}
