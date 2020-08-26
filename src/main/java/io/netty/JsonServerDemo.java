package io.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/26.
 */
public class JsonServerDemo {
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

    public JsonServerDemo(int port) {
        this.port = port;
    }
    private void init(){
        serverBootstrap = new ServerBootstrap();
        /**
         * 为父（接受者）和子（客户端）设置EventLoopGroup。 这些EventLoopGroup用于处理ServerChannel和Channel的所有事件和IO。
         */
        serverBootstrap.group(eventLoopGroup,workEventLoopGroup); //设置2个EventLoopGroup

        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.option(ChannelOption.SO_BACKLOG,100);


        //初始化相关队列
        LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4););


        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast();
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
}
