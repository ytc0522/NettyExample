package com.examples.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class MyHttpServerHandler extends SimpleChannelInboundHandler {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof HttpRequest){

            HttpRequest request = (HttpRequest) msg;
            // 过滤请求
            String uri = request.uri();
            if ("/favicon.ico".equals(uri)){
                return;
            }
            System.out.println("uri = " + uri);

            // 每次请求都会产生一个新的Handler对象。
            System.out.println("this.hashCode " + this.hashCode());


            String content = "this is Http response";

            ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);

            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK, byteBuf);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,byteBuf.readableBytes());

            ctx.writeAndFlush(response);
        }
    }
}
