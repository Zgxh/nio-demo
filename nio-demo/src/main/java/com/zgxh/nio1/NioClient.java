package com.zgxh.nio1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 客户端
 * 事件：连接、读
 *
 * @author Yu Yang
 */
public class NioClient {

    private static final String LOCAL_HOST = "127.0.0.1";
    private static final Integer PORT = 8080;
    private Selector selector;

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                NioClient client = new NioClient();
                client.connect(LOCAL_HOST, PORT);
                client.listen();
            }).start();
        }
    }

    public void connect(String host, int port) {
        try {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            this.selector = Selector.open();
            sc.register(this.selector, SelectionKey.OP_CONNECT);
            sc.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        while (true) {
            try {
                int events = this.selector.select();
                if (events > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isConnectable()) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            if (sc.isConnectionPending()) {
                                sc.finishConnect();
                            }
                            sc.configureBlocking(false);
                            sc.register(this.selector, SelectionKey.OP_READ);
                            sc.write(ByteBuffer.wrap(("hello from client: " + Thread.currentThread().getName()).getBytes()));
                        } else if (key.isReadable()) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                            sc.read(readBuffer);
                            readBuffer.flip();
                            System.out.println("Client receive data from server: " + new String(readBuffer.array()));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
