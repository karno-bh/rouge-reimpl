package il.ac.sce.ir.metric.starter.gui.main.util.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PubSub {

    private final static Logger log = LoggerFactory.getLogger(PubSub.class);

    private final Map<Class<? extends Event>, List<Consumer<Event>>> listeners = new HashMap<>();

    public<T extends Event> void subscribe(Class<T> category, Consumer<T> consumer) {
        List<Consumer<Event>> consumers = listeners.computeIfAbsent(category, c -> new ArrayList<>());
        consumers.add((Consumer<Event>) consumer);
    }

    public void publish(Event event) {
        log.info("PubSub got event: {}", event);
        Class<? extends Event> aClass = event.getClass();
        List<Consumer<Event>> consumers = listeners.get(aClass);
        if (consumers == null || consumers.isEmpty()) {
            throw new IllegalStateException("Trying to publish event that has no subscription. Check your application logic");
        }

        for (Consumer<Event> consumer : consumers) {
            log.info("Publishing to {}", consumer.getClass());
            consumer.accept(event);
        }
    }
}
