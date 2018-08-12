package il.ac.sce.ir.metric;

import java.util.HashMap;
import java.util.Map;

public class CrossProductInMemoryStore implements CrossProductStore {

    private final Map<Tuple2<String, String>, Object> store = new HashMap<>();

    @Override
    public <T> void getData(Tuple2<String, String> crossProductKey, CrossProductValue<T> collector) {
        Object storeValue = store.get(crossProductKey);
        collector.setDotProductValue((T) storeValue);
    }

    @Override
    public <T> void setData(Tuple2<String, String> crossProductKey, CrossProductValue<T> data) {
        store.put(crossProductKey, data.getDotProductValue());
    }
}
