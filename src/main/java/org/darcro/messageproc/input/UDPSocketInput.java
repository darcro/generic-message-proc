package org.darcro.messageproc.input;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPSocketInput extends DataProvider implements Runnable {

    private final DatagramSocket sock;

    private boolean running;

    public UDPSocketInput(int port) throws SocketException {
        super();
        sock = new DatagramSocket(port);
        running = true;
    }

    public void shutdown() {
        running = false;
        sock.close();
    }

    private final byte[] buffer = new byte[4096];

    @Override
    public void run() {
        while (running) {
            try {
                final DatagramPacket p = new DatagramPacket(buffer, buffer.length);
                sock.receive(p);
                final long timestamp = System.currentTimeMillis();
                final byte[] data = new byte[p.getLength()];
                System.arraycopy(p.getData(), 0, data, 0, p.getLength());
                publish(timestamp, data);
            } catch (IOException e) {
                if (running) {
                    System.err.println("Failed to receive data on DatagramSocket: " + e.getMessage());
                    e.printStackTrace();
                    running = false;
                }
            }
        }
    }
}
