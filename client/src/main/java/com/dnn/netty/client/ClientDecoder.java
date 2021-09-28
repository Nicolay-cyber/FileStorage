package com.dnn.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientDecoder extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext chc, String msg) throws Exception {
        System.out.println("Massage from server: " + msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext chc) throws Exception {
        System.out.println("New channel is active");
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected");
    }
}
