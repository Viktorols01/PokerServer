package server;

public class Broadcaster {
    private String message;
    private Object notifier;

    public Broadcaster() {
        this.notifier = new Object();
    }

    public void receive(String message) throws InterruptedException {
        synchronized (notifier) {
            this.notifier.wait();
            if (!this.message.equals(message)) {
                receive(message);
            }
        }
    }

    public String receive() throws InterruptedException {
        synchronized (notifier) {
            this.notifier.wait();
            return message;
        }
    }

    public void broadcast(String message) {
        synchronized (notifier) {
            this.message = message;
            this.notifier.notify();
        }
    }
}
