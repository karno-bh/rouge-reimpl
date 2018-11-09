package il.ac.sce.ir;

import il.ac.sce.ir.metric.core.recollector.CachedMapRecollector;
import il.ac.sce.ir.metric.core.reducer.FileSystemCSVSaver;
import il.ac.sce.ir.metric.core.reducer.NormalizeFleschRedabilityAndWordVariationByCorpus;
import il.ac.sce.ir.metric.core.reducer.ReducerStore;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import org.junit.Test;

import java.util.Map;

public class NormalizedFleschReducerTest {

    @Test
    public void normalizeFleschReducerTest() {
        Configuration configuration = Configuration.as()
                .resultDirectory("c:\\my\\learning\\final-project\\rouge-reimpl\\rouge-reimpl\\result\\")
                .build();
        CachedMapRecollector r = new CachedMapRecollector();
        r.setConfiguration(configuration);
        ReducerStore<Map<String, Object>> reducerStore = new ReducerStore<>(r);
        NormalizeFleschRedabilityAndWordVariationByCorpus normalizeFleschRedabilityAndWordVariationByCorpus = new NormalizeFleschRedabilityAndWordVariationByCorpus();
        normalizeFleschRedabilityAndWordVariationByCorpus.setStore(reducerStore);
        normalizeFleschRedabilityAndWordVariationByCorpus.reduce();

        FileSystemCSVSaver fileSystemCSVSaver = new FileSystemCSVSaver();
        fileSystemCSVSaver.setConfiguration(configuration);
        fileSystemCSVSaver.setReducerStore(reducerStore);

        fileSystemCSVSaver.reduce();
    }
}
