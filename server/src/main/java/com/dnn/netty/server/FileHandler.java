package com.dnn.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

public class FileHandler extends SimpleChannelInboundHandler<Request> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        String filename = msg.getFilename();
        byte[] buffer = new byte[1024 * 512];
        try (RandomAccessFile accessFile = new RandomAccessFile(filename, "r")) {
            while (true) {
                Response response = new Response();
                response.setFilename(filename);
                response.setPosition(accessFile.getFilePointer());
                int read = accessFile.read(buffer);
                if (read < buffer.length - 1) {
                    byte[] tempBuffer = new byte[read];
                    System.arraycopy(buffer, 0, tempBuffer, 0, read);
                    response.setFile(tempBuffer);
                    ctx.writeAndFlush(response);
                    break;
                } else {
                    response.setFile(buffer);
                    ctx.writeAndFlush(response);
                }
                buffer = new byte[1024 * 512];
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
