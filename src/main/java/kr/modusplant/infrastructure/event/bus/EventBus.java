package kr.modusplant.infrastructure.event.bus;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class EventBus {
    private final List<Consumer<Object>> subscribers = new ArrayList<>();

    public void publish(Object event) {
        subscribers.forEach(subscriber -> subscriber.accept(event));
    }

    public void subscribe(Consumer<Object> subscriber) {
        subscribers.add(subscriber);
    }
}
