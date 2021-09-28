package com.dnn.netty.server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Server {
    //private static final String PORT = System.getenv("PORT");
    private static final int PORT = 9000;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws InterruptedException {
        new Server().start();
    }

    private void start() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap server = new ServerBootstrap();
            server
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                new StringDecoder(),
                                new StringEncoder(),
                                new ServerDecoder()
                            );
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = server.bind(HOST,PORT).sync();
            System.out.println("Server is ready on port " + "9000");
            future.channel().closeFuture().sync();
        } finally {
            System.out.println("Server is closed");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
}
}
