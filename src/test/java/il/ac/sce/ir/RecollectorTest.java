package il.ac.sce.ir;

import il.ac.sce.ir.metric.core.recollector.CachedMapRecollector;
import il.ac.sce.ir.metric.core.reducer.ReducerStore;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import org.junit.Test;

import java.util.Map;

public class RecollectorTest {

    @Test
    public void recollectorTest() {
        Configuration c = Configuration.as()
                .resultDirectory("c:\\my\\learning\\final-project\\git\\rouge-reimpl\\result\\")
                .build();
        CachedMapRecollector r = new CachedMapRecollector();
        r.setConfiguration(c);
        r.recollect();
    }

    @Test
    public void storeReducerTest() {
        Configuration c = Configuration.as()
                .resultDirectory("c:\\my\\learning\\final-project\\git\\rouge-reimpl\\result\\")
                .build();
        CachedMapRecollector r = new CachedMapRecollector();
        r.setConfiguration(c);
        ReducerStore<Map<String, Object>> reducerStore = new ReducerStore<>(r);
        reducerStore.updateStore(store -> {
            System.out.println(store);
            return store;
        });
    }
}
