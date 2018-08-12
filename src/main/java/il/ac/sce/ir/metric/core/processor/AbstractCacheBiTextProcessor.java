package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.data.Text;

import java.util.Objects;

public abstract class AbstractCacheBiTextProcessor<X, Y> implements BiTextProcessor<X, Y> {

    private final AbstractCacheBiTextProcessor<X, Y> nextLevelCache;

    private final BiTextProcessor<X, Y> biTextProcessor;

    public AbstractCacheBiTextProcessor(AbstractCacheBiTextProcessor<X, Y> nextLevelCache, BiTextProcessor<X, Y> textProcessor) {
        Objects.requireNonNull(textProcessor, "Bi Text Processor cannot be null");
        this.nextLevelCache = nextLevelCache;
        this.biTextProcessor = textProcessor;
    }

    @Override
    public BiText<Y> process(Text<X> left, Text<X> right) {
        BiText<Y> result = getCached(left.getTextId(), right.getTextId());
        if (result != null) {
            return result;
        }
        if (nextLevelCache != null) {
            result = nextLevelCache.process(left, right);
        } else {
            result = biTextProcessor.process(left, right);
        }
        setToCache(result);
        return result;
    }

    protected abstract BiText<Y> getCached(String leftId, String rightId);

    protected abstract void setToCache(BiText<Y> computed);
}
