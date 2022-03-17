package com.zgxh.nio1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author Yu Yang
 */
public class NioServerHandler implements Runnable {

    private final SelectionKey selectionKey;

    public NioServerHandler(SelectionKey key) {
        this.selectionKey = key;
    }

    @Override
    public void run() {
        if (this.selectionKey.isReadable()) {
            SocketChannel sc = (SocketChannel) selectionKey.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {
                sc.read(buffer);
                buffer.flip();
                System.out.println("Nio server receive data from client: "
                        + sc.socket().getInetAddress().getHostName()
                        + ", data: " + new String(buffer.array()));

                ByteBuffer outputBuffer = ByteBuffer.wrap(buffer.array());
                sc.write(outputBuffer);
                selectionKey.cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
