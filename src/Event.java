public class Event {
    public static final int SERVER_EVENT = -1;

    private final int queueId;
    private final EventType eventType;
    private final double time;

    public Event(EventType eventType, double time) {
        this.eventType = eventType;
        this.time = time;
        queueId = SERVER_EVENT;
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
