package il.ac.sce.ir;

import il.ac.sce.ir.metric.CrossProductInMemoryStore;
import il.ac.sce.ir.metric.CrossProductStore;
import il.ac.sce.ir.metric.CrossProductValue;
import il.ac.sce.ir.metric.Tuple2;
import org.junit.Assert;
import org.junit.Test;

/**
 * Should be removed as core changed. Do not rely on this. Once will be cleared
 */
@Deprecated
public class SimpleStoreTest {

    @Test
    public void testStore001() {
        CrossProductStore store = new CrossProductInMemoryStore();

        Tuple2<String, String> tuple = new Tuple2<>("id001", "id002");
        CrossProductValue<Double> value = new CrossProductValue<>(Double.class);
        value.setDotProductValue(0.3251);

        store.setData(tuple, value);

        CrossProductValue<Double> collector = new CrossProductValue<>(Double.class);
        store.getData(tuple, collector);

        Double dotProductValue = collector.getDotProductValue();

        Assert.assertEquals(0.3251, dotProductValue, 0.00001);

        //store.setData();
    }
}
