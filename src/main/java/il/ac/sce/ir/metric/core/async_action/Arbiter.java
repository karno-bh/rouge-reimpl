package il.ac.sce.ir.metric.core.async_action;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Arbiter {

    private final Collection<String> registeredActions = new HashSet<>();

    private final BlockingQueue<String> drop = new ArrayBlockingQueue<>(32, true);

    public void register(String action) {
        registeredActions.add(action);
    }

    public void signalizeFinish(String message) {
        drop.add(message);
    }

    public void release() {
        try {
            while (!registeredActions.isEmpty()) {
                String message = drop.take();
                registeredActions.removeIf(message::equals);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Queue interrupted", e);
        }
    }

}
