package il.ac.sce.ir.metric.starter.gui.main.util.pubsub;

import il.ac.sce.ir.metric.core.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

public class PubSub {

    private final static Logger log = LoggerFactory.getLogger(PubSub.class);

    private final Map<Class<? extends Event>, List<Consumer<Event>>> listeners = new HashMap<>();

    public<T extends Event> void subscribe(Class<T> category, Consumer<T> consumer) {
        List<Consumer<Event>> consumers = listeners.computeIfAbsent(category, c -> new ArrayList<>());
        consumers.add((Consumer<Event>) consumer);
    }

    public<T extends Event> void unsubscribe(Class<T> category, Consumer<T> consumer) {
        List<Consumer<Event>> consumers = listeners.get(category);
        consumers.removeIf(registered -> registered.equals(consumer));
    }

    public void publish(Event event) {
        log.info("PubSub got event: {}", event);
        Class<? extends Event> aClass = event.getClass();
        List<Consumer<Event>> consumers = listeners.get(aClass);
        if (consumers == null || consumers.isEmpty()) {
            throw new IllegalStateException("Trying to publish an event that has no subscription. Check your application logic");
        }

        for (Consumer<Event> consumer : consumers) {
            log.info("Publishing to {}", consumer.getClass());
            consumer.accept(event);
        }
    }
}
