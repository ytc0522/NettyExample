package com.examples.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    // 通道就绪
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String msg = "Hello,Im Client";
        ctx.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
        System.out.println("客户端已发送消息");
    }


    // 当通道有读取事件时
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String rec = buf.toString(CharsetUtil.UTF_8);
        System.out.println("客户端收到消息:" + rec);
    }
}
