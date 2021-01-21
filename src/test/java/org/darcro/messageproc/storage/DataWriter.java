package org.darcro.messageproc.storage;

import org.darcro.messageproc.storage.proto.StorageProto;

import java.io.FileOutputStream;
import java.io.IOException;

public class DataWriter {

    public static void main(String [] args) {
        try {
            final FileOutputStream fw = new FileOutputStream(args[0]);
            final StorageProto.PacketGroup.Builder groupBuilder = StorageProto.PacketGroup.newBuilder();

            // Add any test packet data here

            fw.write(groupBuilder.build().toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
