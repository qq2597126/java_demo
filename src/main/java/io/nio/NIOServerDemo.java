package io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lcy
 * @DESC:
 * @date 2020/7/30.
 */
public class NIOServerDemo {


    /**
     * 服务端的相关代码。
     */
    class ServerSocketChannelDemo{
        private int port; //端口
        private Selector selector;
        ServerSocketChannel serverSocketChannel;

        private ExecutorService executorService = new ThreadPoolExecutor(5,10,20, TimeUnit.SECONDS,new ArrayBlockingQueue<>(5));


        public ServerSocketChannelDemo(int port) {
            this.port = port;
        }

        public void init() throws IOException {
            //创建选择器
            selector = Selector.open();
            //创建通道
            serverSocketChannel = ServerSocketChannel.open();
            //设置通道为非阻塞
            serverSocketChannel.configureBlocking(false);
            //绑定端口
            serverSocketChannel.bind(new InetSocketAddress(port));
        }

        //连接事件
        public void accept() throws IOException {
            //注册连接的监听事件
            serverSocketChannel.register(selector,SelectionKey.OP_CONNECT);

            serverSocketChannel.accept();
        }

    }
}
