package io.nio;


import com.sun.org.apache.bcel.internal.generic.Select;
import utils.NioUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NioServer {

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer(8080);
        nioServer.start();
    }


    /**
     * 处理任务的线程。
     */
    private ExecutorService work = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>());

    private volatile boolean isStop = false;

    private ServerSocketChannel serverSocketChannel = null;

     //private Selector selector =  Selector.open();

    private AtomicInteger inr = new AtomicInteger(0);

    /**
     *
     */
    private SubReactor[] workThreadHandlers = new SubReactor[Runtime.getRuntime().availableProcessors()];


    /**
     * 端口
     */
    private int port;

    public NioServer(int port) throws IOException {
        this.port = port;
        serverSocketChannel = ServerSocketChannel.open();

        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //绑定端口号
        serverSocketChannel.bind(new InetSocketAddress(port));


        serverSocketChannel.register(Selector.open(),SelectionKey.OP_ACCEPT);

        System.out.println("服务启动成功  端口："+port+" 当前CPU "+Runtime.getRuntime().availableProcessors());

    }
    public void start(){

            try {
                ReactorAcceptor reactorAcceptor = new ReactorAcceptor();
                reactorAcceptor.registerChannel(serverSocketChannel);
                new Thread(reactorAcceptor).start();

                //开启sub
                for (int index = 0;index < workThreadHandlers.length; index ++) {
                    //开启连接线程
                    workThreadHandlers[index] = new SubReactor();
                    new Thread(workThreadHandlers[index]).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

    }





    /**
     * Reactor 处理连接
     */
     class  ReactorAcceptor implements  Runnable{

        private ServerSocketChannel serverSocketChannel ;

        //private Selector selector;

        public void registerChannel(ServerSocketChannel serverSocketChannel) throws Exception {
            /**
             * 连接就绪和读
             */
            //selector = Selector.open();
            this.serverSocketChannel = serverSocketChannel;
            this.serverSocketChannel.configureBlocking(false);
            //this.serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);
        }


        @Override
        public void run() {
            //处理连接请求
            while (!isStop){
                try {
                    /*int select = selector.select();

                    if(select > 0){

                        Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();

                        while (selectionKeyIterator.hasNext()){

                            SelectionKey selectionKey = selectionKeyIterator.next();
                            selectionKeyIterator.remove();
                            if(selectionKey.isAcceptable()){
                                SocketChannel socketChannel = this.serverSocketChannel.accept();
                                if(socketChannel != null){
                                    handler(socketChannel);
                                }
                            }
                        }
                    }*/
                    SocketChannel socketChannel = this.serverSocketChannel.accept();
                    if(socketChannel != null){
                        handler(socketChannel);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



        /**
         * 处理连接请求的任务分发
         * @param socketChannel
         */
        public void handler(SocketChannel socketChannel) throws IOException {
            try{
                int index = inr.get();
                if(index >= workThreadHandlers.length ){
                    inr.set(0);
                }
                inr.addAndGet(1);
                SubReactor workEventLoop = workThreadHandlers[index];
                workEventLoop.registerChannel(socketChannel);
            }catch (IOException e){
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 单线程任务分发
     */
    class SubReactor implements  Runnable{

        private Selector selector = null;

        private final int buffer_default = 1024;


        public SubReactor() throws IOException {
            selector =  Selector.open();
        }

        public void registerChannel(SocketChannel sc) throws Exception {
            /**
             * 连接就绪和读
             */
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        }

        @Override
        public void run() {
            //处理读取和写入请求的
            while(!isStop){
                //每个SubReactor 自己做事件分派处理读写事件
                try {
                    int select = selector.select(100);
                    if(select > 0){
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();
                        while (selectionKeyIterator.hasNext()) {
                            SelectionKey key = selectionKeyIterator.next();
                            selectionKeyIterator.remove();
                            if (key.isReadable()) {
                                read(key);
                            } else if (key.isWritable()) {
                                write(key);
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 读取数据
         * @param selectionKey
         */
        public void read( SelectionKey selectionKey) throws IOException {

            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer requestBuffer = ByteBuffer.allocate(buffer_default);
            while (socketChannel.isOpen() && socketChannel.read(requestBuffer) != -1) { //此处不能放到线程里面，Select检测不到
                // 长连接情况下,需要手动判断数据有没有读取结束 (此处做一个简单的判断: 超过0字节就认为请求结束了)
                if (requestBuffer.position() > 0) break;
            }
            requestBuffer.flip();


            //读取数据并处理业务请求
            work.submit(()->{
                //读取数据
                try {
                    System.out.println(Thread.currentThread().getName()+"- 读 - 开始处理业务逻辑");
                    //简单的打印数据
                    System.out.println(Thread.currentThread().getName()+"- 读 - "+new String(requestBuffer.array(),Charset.forName("utf-8")));
                    Thread.sleep(5000);
                    System.out.println(Thread.currentThread().getName()+"- 读 - 处理完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            // 数据
            write(selectionKey);
        }
        /**
         * 写入数据
         * @param selectionKey
         */
        public  void write( SelectionKey selectionKey){
            //数据
            work.execute(()->{
                try {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    // 响应结果 200
                    String response = "HTTP/1.1 200 OK\r\n" +
                            "Content-Length: 11\r\n\r\n" +
                            "Hello World";
                    ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
                    System.out.println(Thread.currentThread().getName()+"- 写 - 开始处理业务逻辑");
                    while (buffer.hasRemaining()) {
                        socketChannel.write(buffer);
                    }
                    System.out.println(Thread.currentThread().getName()+"- 写 - 处理完成");
                }catch (IOException e){
                    e.printStackTrace();
                }
            });
        }
    }


}
