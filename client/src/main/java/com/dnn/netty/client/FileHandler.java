package com.dnn.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

public class FileHandler extends SimpleChannelInboundHandler<Response> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        try(RandomAccessFile accessFile = new RandomAccessFile("response_" + msg.getFilename(), "rw")) {
            accessFile.seek(msg.getPosition());
            accessFile.write(msg.getFile());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
