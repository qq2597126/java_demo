package io.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerScoketDemo {

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

                InputStream inputStream = accept.getInputStream();
                //读取数据
                try {
                    //BufferedReader bufferedInputStream = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                    //new InputStreamReader(inputStream, "UTF-8");
                    byte[] buf = new byte[5000];
                    int len = 0;
                    while ((len = inputStream.read(buf)) != -1) {
                        System.out.println(getdate() + "  客户端: （"
                                + serverSocket.getInetAddress().getHostAddress() + "）说："
                                + new String(buf, 0, len, "UTF-8"));
                    }
                    //System.out.println("收到的信息来自"+accept.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    accept.close();
                }

            }
        }
        public static String getdate() {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String result = format.format(date);
            return result;
        }
    }
}
