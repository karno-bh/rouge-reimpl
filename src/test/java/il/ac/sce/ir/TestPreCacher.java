package il.ac.sce.ir;

import il.ac.sce.ir.metric.concrete_metric.common.util.ParallelPreCache;
import il.ac.sce.ir.metric.concrete_metric.common.util.SequentialPreCache;
import il.ac.sce.ir.metric.concrete_metric.common.util.TestWithMain;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import org.junit.Test;

public class TestPreCacher {

    @Test
    public void sequentialPreCacherTest() {
        Configuration configuration = Configuration.fromFileName("sep-config.json");
        System.out.println(configuration);
        SequentialPreCache sequentialPreCache = new SequentialPreCache();
        sequentialPreCache.preCache(configuration);
    }

    @Test
    public void parallelPreCacherTest() {
        Configuration configuration = Configuration.fromFileName("sep-config.json");
        System.out.println(configuration);
        ParallelPreCache parallelPreCache = new ParallelPreCache();
        parallelPreCache.preCache(configuration);
    }

    @Test
    public void testWithMainTest() {
        TestWithMain testWithMain = new TestWithMain(0);
        testWithMain.test();
    }
}
