interface Subject {
    void notifySubscribers();
    void addSubscriber(Observer o);
    void removeSubscriber(Observer o);
}