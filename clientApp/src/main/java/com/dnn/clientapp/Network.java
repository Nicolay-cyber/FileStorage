package com.dnn.clientapp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

public class Network {
    ChannelFuture future;
    public void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) {

                            ch.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(
                                            1024 * 1024 * 1024,
                                            0,
                                            8,
                                            0,
                                            8
                                    ),
                                    new LengthFieldPrepender(8),
                                    new ByteArrayDecoder(),
                                    new ByteArrayEncoder(),
                                    new ResponseDecoder(),
                                    new RequestEncoder(),
                                    new ResponseWorker()
                            );
                        }
                    });

            future = client.connect("localhost", 8089).sync();
            System.out.println("Server's connection is ready");
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }


}
