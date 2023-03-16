package com.examples.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class GroupChatClient {

    private static  final int SERVER_PORT = 8888;

    private static  final String  SERVER_HOST = "127.0.0.1";

    public static void main(String[] args) throws InterruptedException {


        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(loopGroup)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("decoder",new StringDecoder())
                                .addLast("encoder",new StringEncoder())
                                .addLast(new GroupChatClientHandler());
                    }
                });

        ChannelFuture future = bootstrap.connect(SERVER_HOST, SERVER_PORT).sync();


        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("客户端连接服务器成功," + future.channel().localAddress());
            }
        });
        Channel channel = future.channel();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            channel.writeAndFlush(line);
        }
    }


}
