package com.examples.netty.groupchat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class GroupChatServer {

    private static Selector selector;

    private static ServerSocketChannel serverChannel;

    private static final int PORT = 6666;

    public GroupChatServer() {
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            // 绑定端口
            serverChannel.bind(new InetSocketAddress(PORT));
            // 设置非阻塞模式
            serverChannel.configureBlocking(false);
            // 注册监听连接事件
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("服务器初始化成功～");
    }

    /**
     * 监听管道中的事件
     *
     * @throws IOException
     */
    public void listen() throws IOException {
        while (true) {
            // 监听管道事件
            int count = selector.select(2);

            if (count > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                // 遍历管道中的事件
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // 判断是否是连接事件
                    if (key.isAcceptable()) {
                        SocketChannel acceptChannel = serverChannel.accept();
                        acceptChannel.configureBlocking(false);
                        // 注册接收到的channel
                        acceptChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println(acceptChannel.getRemoteAddress() + "上线了~");
                    }
                    // 判断是否是读取事件
                    if (key.isReadable()) {
                        readClient(key);
                    }
                    iterator.remove();
                }
            }
        }
    }


    /**
     * 读取客户端发送的数据
     *
     * @param selectionKey
     */
    public void readClient(SelectionKey selectionKey) {
        SocketChannel channel = (SocketChannel) selectionKey.channel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int count = 0;
        try {
            count = channel.read(buffer);
            if (count > 0) {
                String msg = new String(buffer.array());
                System.out.println("接收到消息：" + msg.trim());

                // 再将消息发送给其他客户端
                sendToOtherClients(msg, channel);
            }
        } catch (IOException e) {
            // 有可能客户端会关闭连接
            try {
                System.out.println(channel.getRemoteAddress() + "离线～");
                // 取消注册
                selectionKey.cancel();
                // 关闭通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

    }

    private void sendToOtherClients(String msg, SocketChannel self) throws IOException {
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            Channel other = key.channel();
            if (other instanceof SocketChannel && other != self)
                ((SocketChannel)other).write(ByteBuffer.wrap(msg.getBytes()));
        }
    }

    public static void main(String[] args) throws IOException {
        new GroupChatServer().listen();
    }


}
