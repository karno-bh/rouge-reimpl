package il.ac.sce.ir.metric.core.utils.switch_obj;

import java.util.HashMap;
import java.util.Map;

public class SwitchObj<T> {

    private final Map<T, Runnable> switches = new HashMap<>();

    private Runnable defaultAction = () -> {};

    public SwitchObj<T> on(T key, Runnable runnable) {
        this.switches.put(key, runnable);
        return this;
    }

    public SwitchObj<T> onDefault(Runnable runnable) {
        this.defaultAction = runnable;
        return this;
    }

    public void doSwitch(T key) {
        Runnable action = switches.get(key);
        if (action == null) {
            action = defaultAction;
        }
        action.run();
    }

}
