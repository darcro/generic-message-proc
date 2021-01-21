package org.darcro.messageproc.input;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class TestSocketReader {

    @Test(timeout = 5000L)
    public void testSocketRead() throws IOException, InterruptedException {
        final Random r = new Random(System.currentTimeMillis());
        final byte[] testData = new byte[1024];
        r.nextBytes(testData);

        final DatagramSocket internalSender = new DatagramSocket(13337);

        final int listenPort = 36665;
        final UDPSocketInput sr = new UDPSocketInput(listenPort);
        final Thread thread = new Thread(sr);
        thread.start();
        ArrayList<ByteBuffer> outputs = new ArrayList<>(8);

        final DataProvider.DataListener listener = (timestamp, data) -> outputs.add(ByteBuffer.wrap(data));

        sr.register(listener);

        DatagramPacket outboundPacket = new DatagramPacket(testData, testData.length, InetAddress.getLoopbackAddress(), listenPort);
        internalSender.send(outboundPacket);

        while(outputs.size() < 1) {
            Thread.sleep(100);
        }
        assertEquals(1, outputs.size());
        assertArrayEquals(testData, outputs.get(0).array());

        sr.shutdown();
    }

}

