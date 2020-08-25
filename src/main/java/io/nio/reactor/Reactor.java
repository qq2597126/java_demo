package io.nio.reactor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

class Reactor implements Runnable {

    Selector selector;
    ServerSocketChannel serverSocket;


    private Reactor() throws Exception {

        SelectionKey sk =
                serverSocket.register(selector,
                        SelectionKey.OP_ACCEPT);
        // attach Acceptor 处理新连接
        sk.attach(new Acceptor());
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                selector.select();
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext()) {
                    it.remove();
                    //分发事件处理
                    dispatch((SelectionKey) (it.next()));
                }
            }
        } catch (IOException ex) {
            //do something
        }
    }

    void dispatch(SelectionKey k) {
        // 若是连接事件获取是acceptor
        // 若是IO读写事件获取是handler
        Runnable runnable = (Runnable) (k.attachment());
        if (runnable != null) {
            runnable.run();
        }
    }

    class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel c = serverSocket.accept();
                if (c != null) {// 注册读写
                    new EchoHandler(selector,c);
                }
            } catch (Exception e) {

            }
        }
    }

}
