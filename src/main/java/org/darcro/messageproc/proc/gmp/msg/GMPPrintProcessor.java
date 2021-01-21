package org.darcro.messageproc.proc.gmp.msg;

import org.darcro.messageproc.gmp.proto.GMPMessages;
import org.darcro.messageproc.proc.MessageProcessor;

/**
 * This PrintProcessor is a basic implementation of a MessageProcessor
 * that prints each message is processes.
 */
public class GMPPrintProcessor implements MessageProcessor<GMPMessages.GMPMessage > {

    public GMPPrintProcessor() {

    }

    @Override
    public boolean process(GMPMessages.GMPMessage msg) {
        System.out.print(msg);
        return true;
    }

    @Override
    public void shutdown() {

    }
}
