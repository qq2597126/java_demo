package io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lcy
 * @DESC:
 * @date 2020/5/13.
 */
public class NioServerDemo {
    private int port;
    //通道管理器
    private Selector selector;
    /**
     * 1. 核心线程数
     * 2. 最大线程数
     * 3.非核心线程数存活时间
     * 4.单位
     * 5.存储数据的队列
     */
    private ExecutorService executorService = new ThreadPoolExecutor(5,10,20, TimeUnit.SECONDS,new ArrayBlockingQueue<>(5));

    public  NioServerDemo(int port){
        this.port = port;
    }
    public void init() throws IOException {
        //创建选择器
        selector = Selector.open();
        //创建创建通道
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        //设置为非阻塞模式
        socketChannel.configureBlocking(false);
        socketChannel.bind(new InetSocketAddress(port));
        // 事件注册
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);


    }

    public void accept(SelectionKey key) {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            //sc.register(selector, SelectionKey.OP_READ);
            System.out.println("accept a client : " + sc.socket().getInetAddress().getHostName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        channel.read(buffer);
        buffer.flip();
        System.out.println("收到客户端"+channel.socket().getInetAddress().getHostName()+"的数据："+new String(buffer.array()));

        ByteBuffer outBuffer = ByteBuffer.wrap(buffer.array());

        channel.write(outBuffer);// 将消息回送给客户端
        //key.cancel();
    }


    public void start() throws IOException {
        this.init();
        while (true) {
            try {
                int events = selector.select();
                System.out.println(111111);
                if (events > 0) {
                    System.out.println(222222);
                    Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
                    while (selectionKeys.hasNext()) {
                        SelectionKey key = selectionKeys.next();
                        selectionKeys.remove();
                        if (key.isAcceptable()) {
                            accept(key);
                        } else {
                            read(key);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        NioServerDemo serverDemo = new NioServerDemo(8080);
        serverDemo.start();
    }
}