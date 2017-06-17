package events;

import java.util.Comparator;
import java.util.PriorityQueue;

public class EventList {
    private PriorityQueue<Event> events;

    public EventList() {
        this.events = new PriorityQueue<>(5, Comparator.comparingDouble(Event::getTime));
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public Event popNextEvent() {
        return events.poll();
    }

    public Event peekNextEvent() {
        return events.peek();
    }

    public boolean isEmpty() {
        return events.isEmpty();
    }
}
