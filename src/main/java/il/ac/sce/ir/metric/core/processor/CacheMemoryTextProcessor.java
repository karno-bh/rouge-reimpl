package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMemoryTextProcessor<X, Y> extends AbstractCacheTextProcessor<X, Y> {

    protected final Map<String, Text<Y>> cache = new ConcurrentHashMap<>();

    public CacheMemoryTextProcessor(TextProcessor<X, Y> textProcessor) {
        super(textProcessor);
    }

    @Override
    protected Text<Y> getCached(Text<X> data) {
        return cache.get(data.getTextId());
    }

    @Override
    protected void setToCache(Text<Y> computed) {
        Objects.requireNonNull(computed, "Computed value cannot be null");
        String textId = computed.getTextId();
        cache.put(textId, computed);
    }
}
