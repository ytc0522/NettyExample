package com.examples.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class GroupChatClient {

    private final int SERVER_PORT = 6666;

    private final String SERVER_HOST = "127.0.0.1";

    private SocketChannel socketChannel;

    private Selector selector;

    // 连接到服务端
    public GroupChatClient() {
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg(String msg) throws IOException {
        socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
    }

    public void readMsg() throws IOException {
        int readCount = selector.select();
        if (readCount > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel)key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.read(buffer);
                    String msg = new String(buffer.array());
                    System.out.println(msg.trim());
                }
                iterator.remove();
            }
        }
    }

    public static void main(String[] args) {

        GroupChatClient client = new GroupChatClient();
        new Thread(()->{
            while (true){
                try {
                    client.readMsg();
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String msg = scanner.nextLine();
            try {
                client.sendMsg(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
