package com.examples.netty.http;

import com.examples.netty.simple.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class MyHttpServer {

    private final static int port = 7777;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // 启动对象
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(null)  // 这个handler对应bossgroup
                .childHandler(new ChannelInitializer<SocketChannel>() { // 这个handler对应workgroup
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //ch.pipeline().addLast(new NettyServerHandler());
                        // 编解码器
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
                        // 增加处理器
                        pipeline.addLast("MyHttpServerHandler",new MyHttpServerHandler());
                    }
                });

        ChannelFuture future = bootstrap.bind(port).sync();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("Netty绑定端口成功，端口号：" + port);
                System.out.println("Netty Server is Ready!");
            }
        });

        // 对关闭通道进行监听
        future.channel().closeFuture().sync();
    }

}
