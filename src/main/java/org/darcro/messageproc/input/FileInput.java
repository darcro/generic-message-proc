package org.darcro.messageproc.input;

import org.darcro.messageproc.storage.proto.StorageProto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileInput extends DataProvider implements Runnable {

    private boolean running;
    private final FileInputStream in;

    public FileInput(String path) throws FileNotFoundException {
        super();
        in = new FileInputStream(path);
        running = true;
    }

    public void shutdown() {
        running = false;
        try {
            in.close();
        } catch (IOException e) {
            System.err.println("Error while closing file reader: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                final byte[] rawContents = in.readAllBytes();
                final StorageProto.PacketGroup packetGroup = StorageProto.PacketGroup.parseFrom(rawContents);
                packetGroup.getPacketList().forEach(p -> publish(p.getTimestamp(), p.getPayload().toByteArray()));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Failed to read data from file: " + e.getMessage());
                    e.printStackTrace();
                    running = false;
                }
            }
        }
    }
}
