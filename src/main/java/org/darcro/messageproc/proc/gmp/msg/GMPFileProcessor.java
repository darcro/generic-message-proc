package org.darcro.messageproc.proc.gmp.msg;

import org.darcro.messageproc.gmp.proto.GMPMessages;
import org.darcro.messageproc.proc.MessageProcessor;
import org.darcro.messageproc.storage.proto.StorageProto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This FileProcessor logs all messages to a file.
 */
public class GMPFileProcessor implements MessageProcessor<GMPMessages.GMPMessage > {

    public static final int FILE_ROLLOVER_SIZE = 100000; //100kb

    private StorageProto.MessageGroup.Builder messageGroup;
    private int accumulatedSize;

    public GMPFileProcessor() {
        this.messageGroup = StorageProto.MessageGroup.newBuilder();
    }

    @Override
    public boolean process(GMPMessages.GMPMessage msg) {
        messageGroup.addGmpMessage(msg);
        accumulatedSize += msg.getSerializedSize();
        if (accumulatedSize > FILE_ROLLOVER_SIZE) {
            try {
                // Write to file
                final Date now = new Date(System.currentTimeMillis());
                final String filename = new SimpleDateFormat("yyyyMMddHHmm'.txt'").format(now);
                final FileOutputStream fos = new FileOutputStream(filename);
                fos.write(messageGroup.build().toByteArray());
                fos.close();

                // Rollover to new group
                messageGroup = StorageProto.MessageGroup.newBuilder();
                accumulatedSize = 0;
            } catch (IOException e) {
                System.err.println("Unable to write data to file: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    public void shutdown() {

    }
}
