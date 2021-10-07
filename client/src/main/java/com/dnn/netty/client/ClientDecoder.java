package com.dnn.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
public class ClientDecoder extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext chc) throws Exception {
        System.out.println("New channel is active");
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Client caught " + cause.getClass().getName());
    }
}
