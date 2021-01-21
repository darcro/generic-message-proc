package org.darcro.messageproc.proc.gmp.data;

import com.google.protobuf.InvalidProtocolBufferException;
import org.darcro.messageproc.gmp.proto.GMPMessages;
import org.darcro.messageproc.proc.DataProcessor;


public class GMPMessageParser extends DataProcessor<GMPMessages.GMPMessage > {

    public GMPMessageParser() {
    }

    @Override
    public void packet(long timestamp, byte[] data) {
        try {
            final GMPMessages.GMPMessage msg = GMPMessages.GMPMessage.parseFrom(data);
            publish(msg);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Unable to parse payload to protobuf: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {

    }
}
