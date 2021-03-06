package com.dnn.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.ConnectException;
import java.net.SocketException;

public class ServerConnection {
    private io.netty.channel.socket.SocketChannel channel = null;
    private static final int PORT = 17862;
    private static final String HOST = "172.16.198.182";
    boolean isConnectionReady = false;
    public ServerConnection(){
        new Thread(() ->{
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try{
                Bootstrap client = new Bootstrap();
                client.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                channel = ch;
                                ch.pipeline().addLast(
                                    new HttpRequestDecoder(),
                                    new HttpRequestEncoder()
                                        //new ClientDecoder()
                                );
                            }
                        });
                ChannelFuture future = client.connect(HOST, PORT).sync();
                System.out.println("Client is started");
                isConnectionReady = true;
                while (true){
                    channel.writeAndFlush("HI!");
                    Thread.sleep(3000);
                }
                //future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                System.out.println("!!");
            }
            finally {
                workerGroup.shutdownGracefully();
            }
        }).start();
    }

    public void sendMsg(String msg){
        channel.writeAndFlush(msg);
    }
    public boolean isConnectionReady(){
        return isConnectionReady;

    }
}
