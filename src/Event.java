public class Event {
    private EventType eventType;
    private double time;

    public Event(EventType eventType, double time) {
        this.eventType = eventType;
        this.time = time;
    }

    public EventType getEventType() {
        return eventType;
    }

    public double getTime() {
        return time;
    }
}
