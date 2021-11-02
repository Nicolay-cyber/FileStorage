package com.dnn.clientapp.network;

import com.dnn.clientapp.ClientAppController;
import com.dnn.clientapp.network.requestWorks.Request;
import com.dnn.clientapp.network.requestWorks.RequestEncoder;
import com.dnn.clientapp.network.responseWorks.Callback;
import com.dnn.clientapp.network.responseWorks.ResponseDecoder;
import com.dnn.clientapp.network.responseWorks.ResponseWorker;
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
    NioEventLoopGroup group;
    ResponseWorker responseWorker;
    boolean isConnectionReady = false;
    public void sendClientCtrl(ClientAppController clientCtrl) {
        responseWorker.setClientCtrl(clientCtrl);
    }

    public void start(Callback callback) throws InterruptedException {
        group = new NioEventLoopGroup();
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
                                    responseWorker = new ResponseWorker(callback)
                            );
                        }
                    });

            future = client.connect("localhost", 8089).sync();
            System.out.println("Server's connection is ready");
            isConnectionReady = true;
            future.channel().closeFuture().sync();
        }
        finally {
            group.shutdownGracefully();
        }
    }
    public void fullDisconnect(){
        closeChannel();
        group.shutdownGracefully();
        System.exit(1);
    }
    public void closeChannel(){
        group.shutdownGracefully();
    }
    public void sendMsg(Request request) {
        future.channel().writeAndFlush(request);
    }
    public boolean isConnectionReady(){
        return isConnectionReady;
    }
}
