package com.examples.bio;


import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 用来演示BIO server
public class BIOServer {

    public static void main(String[] args) throws IOException {


        ServerSocket serverSocket = new ServerSocket(6666);

        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        while (true) {
            System.out.println("等待客户端连接～");
            Socket socket = serverSocket.accept();
            System.out.println("有一个客户端连接上来了...");
            newCachedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    handle(socket);
                }
            });
        }
    }

    public static void handle(Socket socket) {
        try {
            byte[] bytes = new byte[1024];
            InputStream inputStream = socket.getInputStream();

            while (true) {
                int read = inputStream.read(bytes);
                if (read != -1) {
                    String str = new String(bytes, 0, read);
                    System.out.println("接收到数据:" + str);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
