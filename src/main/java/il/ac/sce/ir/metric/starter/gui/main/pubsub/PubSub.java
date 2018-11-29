package il.ac.sce.ir.metric.starter.gui.main.pubsub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubSub {

    private final Map<Class<? extends Event>, List<Runnable>> listeners = new HashMap<>();

    public void subscribe(Class<? extends Event> category, Runnable runnable) {
        List<Runnable> runnables = listeners.computeIfAbsent(category, c -> new ArrayList<>());
        runnables.add(runnable);
    }

    public void publish(Event event) {
        Class<? extends Event> aClass = event.getClass();
        List<Runnable> runnables = listeners.get(aClass);
        if (runnables == null || runnables.isEmpty()) {
            throw new IllegalStateException("Trying to publish event that has no subscription. Check your application logic");
        }
        runnables.forEach(Runnable::run);
    }
}
