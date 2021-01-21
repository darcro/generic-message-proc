package org.darcro.messageproc.proc;

import org.darcro.messageproc.input.DataProvider;
import org.darcro.messageproc.util.Shutdownable;

import java.util.ArrayList;

/**
 * A {@code DataProcessor} can be registered to a {@code Provider} to
 * process byte[] into some form of Message.
 * @param <T> Type of Output this data processor produces.
 */
public abstract class DataProcessor<T> implements Shutdownable, DataProvider.DataListener{

    private final ArrayList<MessageProcessor<T>> messageProcessors;

    protected DataProcessor() {
        this.messageProcessors = new ArrayList<>();
    }
    public void register(MessageProcessor<T> messageProcessor){
        messageProcessors.add(messageProcessor);
    }

    public void remove(MessageProcessor<T> messageProcessor){
        messageProcessors.remove(messageProcessor);
    }

    protected void publish(T message) {
        messageProcessors.forEach(proc -> proc.process(message));
    }

    public void shutdown() {
        messageProcessors.forEach(Shutdownable::shutdown);
    }

}