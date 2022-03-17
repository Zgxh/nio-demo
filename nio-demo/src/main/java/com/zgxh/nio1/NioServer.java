package com.zgxh.nio1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Yu Yang
 */
public class NioServer {

    private final int port;
    private Selector selector;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);

    public NioServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        new NioServer(8080).start();
    }

    public void init() {
        ServerSocketChannel ssc;
        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(port));
            this.selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Nio server started ...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept(SelectionKey key) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        try {
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            System.out.println("Nio server accepted a socket connection from client: "
                    + sc.socket().getInetAddress().getHostName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.init();
        while (true) {
            int events = 0;
            try {
                events = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (events > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove(); // safe remove
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else {
                        threadPool.submit(new NioServerHandler(key));
                    }
                }
            }
        }
    }
}
