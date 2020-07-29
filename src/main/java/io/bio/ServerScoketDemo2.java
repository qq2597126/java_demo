package io.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.concurrent.*;

public class ServerScoketDemo2 {

    private static ExecutorService        executor =  new ThreadPoolExecutor(10,20,1000, TimeUnit.SECONDS,new LinkedBlockingQueue());

    public static void main(String[] args) throws IOException {
        BIOServerSocket.create(8080);
    }


    static class BIOServerSocket{
        //端口号
        private static int port;



        public static void create(int port) throws IOException {
            BIOServerSocket.port = port;

            //创建连接
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务端创建成功 端口："+port);
            while (!serverSocket.isClosed()){
                //等待用户连接
                Socket accept = serverSocket.accept(); //阻塞方法
                System.out.println("连接成功。。。。");

               executor.execute(()->{
                   //读取数据
                   try {
                       InputStream inputStream = accept.getInputStream();
                       BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                       String msg = "";
                       while ( (msg = bufferedInputStream.readLine()) != null){
                           if("bye".equals(msg)){
                               break;
                           }
                           System.out.println(msg);
                       }
                       System.out.println("收到的信息来自"+accept.toString());
                   }catch (Exception e){
                       e.printStackTrace();
                   }finally {
                       try {
                           accept.close();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               });

            }
        }

    }
}
