package io.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class BIOClient {
    private static Charset charset = Charset.forName("utf-8");

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1",8080);

        OutputStream outputStream = socket.getOutputStream();



        for (int x = 1000; x >0; x--){
            outputStream.write("hello".getBytes(charset));
            outputStream.flush();
        }
        System.out.println("输入数据：bye 结束");
        Scanner scanner = new Scanner(System.in);
        String nextLine = scanner.nextLine();
        outputStream.close();
    }
}
