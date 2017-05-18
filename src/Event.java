public class Event {
    private int queueId;
    private EventType eventType;
    private double time;

    public Event(EventType eventType, double time) {
        this.eventType = eventType;
        this.time = time;
        queueId = -1; //TODO: Probably bad smell
    }

    public Event(EventType eventType, double time, int queueId) {
        this.eventType = eventType;
        this.time = time;
        this.queueId = queueId;
    }

    public int getQueueId() {
        return queueId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public double getTime() {
        return time;
    }
}
