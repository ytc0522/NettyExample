package com.examples.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

public class GroupChatServer {


    private static final int PORT = 8888;

    public static void main(String[] args) throws InterruptedException {


        ServerBootstrap bootstrap = new ServerBootstrap();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("decoder",new StringDecoder())
                                .addLast("encoder",new StringDecoder())
                                .addLast(new GroupChatServerHandler());
                    }
                });

        ChannelFuture future = bootstrap.bind(PORT).sync();
        System.out.println("服务端启动成功");

        // 对关闭通道进行监听
        future.channel().closeFuture().sync();

    }

}
