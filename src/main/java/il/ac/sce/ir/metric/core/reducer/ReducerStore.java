package il.ac.sce.ir.metric.core.reducer;

import il.ac.sce.ir.metric.core.recollector.Recollector;

import java.util.function.Function;

public class ReducerStore<T> {

    private T store;

    private Recollector<T> recollector;

    public ReducerStore(Recollector<T> recollector) {
        this.recollector = recollector;
    }

    public T getStore() {
        if (store == null) {
            store = recollector.recollect();
        }
        return store;
    }

    public synchronized void updateStore(Function<T,T> storeUpdater) {
        store = storeUpdater.apply(store);
    }
}
