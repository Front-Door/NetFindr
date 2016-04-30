package nz.frontdoor.netfindr;

import java.util.Objects;

/**
 * Created by danielbraithwt on 4/30/16.
 */
public class ThreadEvent {
    private final Object lock = new Object();

    public void signal() {
        synchronized (lock) {
            lock.notify();
        }
    }

    public void await() throws InterruptedException {
        synchronized (lock) {
            lock.wait();
        }
    }
}
