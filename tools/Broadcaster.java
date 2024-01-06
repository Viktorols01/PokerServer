package tools;

public class Broadcaster {
    private Object notifier;

    public Broadcaster() {
        this.notifier = new Object();
    }

    public void receive() throws InterruptedException {
        synchronized (notifier) {
            this.notifier.wait();
        }
    }

    public void broadcast() {
        synchronized (notifier) {
            this.notifier.notify();
        }
    }
}