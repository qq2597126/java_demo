package io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.Date;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/3.
 */
public class NettyServerDemo {



    /**
     * 端口
     */
    private int port;

    /**
     * 1.负责请求接收,参数是线程的个数 （请求连接的EventLoop线程组（相当于NIO中的Select。 进行多线程））
     * 2.线程个数的默认值为 当前CPU核数*2
     * 3.合适线程数 1.创建的时候。 2. io.netty.eventLoopThreads 通过设置参数。
     */
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    /**
     * 工作线程
     */
    private EventLoopGroup workEventLoopGroup = new NioEventLoopGroup();


    private ServerBootstrap serverBootstrap =  null;


    public static void main(String[] args) {
        NettyServerDemo nettyServerDemo = new NettyServerDemo();
        nettyServerDemo.bind(8080);
        nettyServerDemo.start();
    }

    public void bind(int port){
        this.port = port;
        init();
    }
    public  void start(){
        init();
    }

    private void init(){
        serverBootstrap = new ServerBootstrap();
        /**
         * 为父（接受者）和子（客户端）设置EventLoopGroup。 这些EventLoopGroup用于处理ServerChannel和Channel的所有事件和IO。
         */
        serverBootstrap.group(eventLoopGroup,workEventLoopGroup); //设置2个EventLoopGroup

        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.option(ChannelOption.SO_BACKLOG,100);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new EchoServerHandler());
            }
        });
        // 通过bind启动服务
        try {
            ChannelFuture channelFuture= serverBootstrap.bind(port).sync();
            // 阻塞主线程，知道网络服务被关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }

    }



    private class EchoServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("注册事件");
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("取消注册");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            SocketChannel channel = (SocketChannel) ctx.channel();
            System.out.println("链接报告开始");
            System.out.println("链接报告信息：有一客户端链接到本服务端");
            System.out.println("链接报告IP:" + channel.localAddress().getHostString());
            System.out.println("链接报告Port:" + channel.localAddress().getPort());
            System.out.println("链接报告完毕");
            //通知客户端链接建立成功
            String str = "通知客户端链接建立成功" + " " + new Date() + " " + channel.localAddress().getHostString() + "\r\n";
            String data = new Date().toString()+": 时间停止";
            ctx.writeAndFlush(Unpooled.copiedBuffer(data.getBytes()));
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            SocketChannel channel = (SocketChannel) ctx.channel();
            System.out.println("失去连接:"+channel.localAddress().getHostString());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("收到的数据："+new String(ByteBufUtil.getBytes((ByteBuf)msg),"utf-8"));
            //Buf不会被复用
            ctx.write(Unpooled.copiedBuffer("真正的857 857".getBytes()));
            ctx.write(Unpooled.copiedBuffer("真正的857 857".getBytes()));
            ctx.flush();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            //读取完成
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
