package org.darcro.messageproc.proc;

import org.darcro.messageproc.util.Shutdownable;

/**
 * A {@code MessageProcessor} can be registered to one or more {@code DataProcessors} that
 * produce data of the specified type T.
 * @param <T> Type of Message to process
 */
public interface MessageProcessor<T> extends Shutdownable {
    boolean process(T message);
}