package io.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerScoketDemo3 {

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
                           if(msg.length() == 0){
                               break;
                           }
                           System.out.println(msg);
                       }
                       System.out.println("收到的信息来自"+accept.toString());
                       OutputStream outputStream = accept.getOutputStream();
                       outputStream.write("HTTP/1.1 200 OK\r\n ".getBytes());

                       outputStream.write("Content-Length: 11 \r\n\r\n".getBytes());

                       outputStream.write("hello world".getBytes());


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
