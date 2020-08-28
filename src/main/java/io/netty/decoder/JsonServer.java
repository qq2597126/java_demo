package io.netty.decoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author lcy
 * @DESC:
 * @date 2020/8/28.
 */
public class JsonServer {

    //端口号
    private  int port;

    // 处理接收的线程  (父子线程)
    private NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    //工作线程
    private NioEventLoopGroup workEventLoopGroup = new NioEventLoopGroup();

    private ServerBootstrap  serverBootstrap = null;

    public JsonServer(int port) {
        this.port = port;
    }

    public void start(){
        init(port);
    }

    private void init(int port){
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(eventLoopGroup,workEventLoopGroup);
        serverBootstrap.option(ChannelOption.SO_BACKLOG,100);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new JsonChannelInitializer());
        bind(port);
    }
    private void bind(int port){
        try {
            ChannelFuture channelFuture= serverBootstrap.bind(port).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("启动成功 端口："+port);
                    }
                }
            });
            // 阻塞主线程，知道网络服务被关闭
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        JsonServer jsonServer = new JsonServer(8080);
        jsonServer.start();
    }
}
