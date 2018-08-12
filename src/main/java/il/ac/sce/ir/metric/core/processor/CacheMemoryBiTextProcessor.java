package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.processor.AbstractCacheBiTextProcessor;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;

import java.util.HashMap;
import java.util.Map;

public class CacheMemoryBiTextProcessor<X, Y> extends AbstractCacheBiTextProcessor<X, Y> {

    private final Map<String, Map<String, BiText<Y>>> cache = new HashMap<>();

    public CacheMemoryBiTextProcessor(AbstractCacheBiTextProcessor<X, Y> nextLevelCache, BiTextProcessor<X, Y> biTextProcessor) {
        super(nextLevelCache, biTextProcessor);
    }

    public CacheMemoryBiTextProcessor(BiTextProcessor<X, Y> biTextProcessor) {
        super(null, biTextProcessor);
    }

    @Override
    protected BiText<Y> getCached(String leftId, String rightId) {
        Map<String, BiText<Y>> leftSubCache = cache.get(leftId);
        if (leftSubCache == null) {
            return null;
        }
        BiText<Y> cachedBiText = leftSubCache.get(rightId);
        if (cachedBiText == null) {
            return null;
        }
        return cachedBiText;
    }

    @Override
    protected void setToCache(BiText<Y> computed) {
        String leftId = computed.getLeftId();
        String rightId = computed.getRightId();
        Map<String, BiText<Y>> subCache = cache.get(leftId);
        if (subCache == null) {
            subCache = new HashMap<>();
            cache.put(leftId, subCache);
        }
        subCache.put(rightId, computed);
    }
}
