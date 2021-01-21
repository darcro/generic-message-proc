package org.darcro.messageproc.util;

public class QueueOverflowException extends Exception{

    public QueueOverflowException(String message) {
        super(message);
    }
}
