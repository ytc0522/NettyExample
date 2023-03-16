package com.examples.netty.groupchat;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

public class GroupChatServerHandler extends ChannelInboundHandlerAdapter {

    // 管理所有的channel
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    // 当客户端连接建立后，第一个执行
    // 用来表示，某个客户端加入了群聊
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        ctx.writeAndFlush(Unpooled.copiedBuffer("您已经成功加入群聊", CharsetUtil.UTF_8));
        channelGroup.add(channel);
        System.out.println(channel.remoteAddress() + "handler added");

    }


    // 当断开连接时
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "handler removed");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了～");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "离线了～");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端接收到客户端发送的消息：" + msg);
        Channel currCh = ctx.channel();
        // 给其他客户端发送消息
        channelGroup.forEach((channel -> {
            String content = null;
            if (currCh != channel) {
                content = "[" + currCh.remoteAddress() + "]：" + msg;
            } else {
                content = "自己发送的消息：" + msg;
            }
            channel.writeAndFlush(Unpooled.copiedBuffer(content,CharsetUtil.UTF_8));
        }));
    }

//    @Override
//    protected void channelRead(ChannelHandlerContext ctx, String msg) throws Exception {
//        System.out.println("服务端接收到客户端发送的消息：" + msg);
//        Channel currCh = ctx.channel();
//        // 给其他客户端发送消息
//        channelGroup.forEach((channel -> {
//            if (currCh != channel){
//                channel.writeAndFlush("[" + currCh.remoteAddress() + "]：" + msg);
//            } else {
//                channel.writeAndFlush("[自己]发送消息" + msg);
//            }
//        }));
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
