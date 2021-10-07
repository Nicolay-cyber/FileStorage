package com.dnn.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.util.Scanner;

public class ConnectionWithServer {

    private static final int PORT = 9000;
    private static final String HOST = "localhost";
    private ChannelFuture future;
    public ConnectionWithServer() throws InterruptedException {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
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
                                    new JsonDecoder(),
                                    new JsonEncoder()
                                    );
                        }
                    });
            future = client.connect(HOST, PORT).sync();
            System.out.println("Connection with server is ready");
            cmd();
        } finally {
            System.out.println("Server is closed");
            workerGroup.shutdownGracefully();
        }
    }
    private void cmd(){
        Scanner scanner = new Scanner(System.in);
        while (true){
            String cmd = scanner.nextLine();
            String [] msg = cmd.split(" ");
            switch (msg[0]){
                case "getFile":{
                    Request request = new Request();
                    request.requestOnFile(msg[1]);
                    sendReq(request);
                    break;
                }
                case "quit":{
                    future.channel().close();
                    System.exit(1);
                    break;
                }
                default:{
                    System.out.println("unknown command");
                }
            }
        }
    }
    private void sendReq(Request req){
        future.channel().writeAndFlush(req);
    }
}
