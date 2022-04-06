package com.zgxh;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Date;

/**
 * @author Yu Yang
 */
public class NettyClient {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                });

        bootstrap.connect(IP, PORT).addListener(future -> doListen(bootstrap, (ChannelFuture) future));
    }

    /**
     * 失败重连
     * @param bootstrap
     * @param future
     * @throws InterruptedException
     */
    private static void doListen(Bootstrap bootstrap, ChannelFuture future) throws InterruptedException {
        if (future.isSuccess()) {
            System.out.println("连接成功!");
        } else {
            System.out.println("连接失败，正在重试");
            Thread.sleep(2000);
            bootstrap.connect(IP, PORT).addListener(future1 -> doListen(bootstrap, (ChannelFuture) future1));
        }
    }
}
