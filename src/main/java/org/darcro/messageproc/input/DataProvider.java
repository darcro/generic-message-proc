package org.darcro.messageproc.input;

import org.darcro.messageproc.util.Shutdownable;

import java.util.ArrayList;

public abstract class DataProvider implements Shutdownable {

    private final ArrayList<DataListener> dataListeners;

    protected DataProvider() {
        dataListeners = new ArrayList<>();
    }

    public interface DataListener {
        void packet(long timestamp, byte[] data);
    }

    public void register(DataListener listener) {
        dataListeners.add(listener);
    }

    public void remove(DataListener listener) {
        dataListeners.remove(listener);
    }

    protected void publish(long timestamp, byte[] data) {
        dataListeners.forEach(listener -> listener.packet(timestamp, data));
    }
}
