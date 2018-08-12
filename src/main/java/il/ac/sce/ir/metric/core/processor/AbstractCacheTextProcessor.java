package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;

import java.util.Objects;

public abstract class AbstractCacheTextProcessor<X, Y> implements TextProcessor<X, Y> {

    private final AbstractCacheTextProcessor<X, Y> nextLevelCache;

    private final TextProcessor<X, Y> textProcessor;

    public AbstractCacheTextProcessor(AbstractCacheTextProcessor<X, Y> nextLevelCache, TextProcessor<X, Y> textProcessor) {
        Objects.requireNonNull(textProcessor, "Text Processor should not be null");
        this.textProcessor = textProcessor;
        this.nextLevelCache = nextLevelCache;
    }

    @Override
    public Text<Y> process(Text<X> data) {
        Text<Y> result = getCached(data);
        if (result != null) {
            return result;
        }
        if (nextLevelCache != null) {
            result = nextLevelCache.process(data);
        } else {
            result = textProcessor.process(data);
        }
        setToCache(result);
        return result;
    }

    protected abstract Text<Y> getCached(Text<X> data);

    protected abstract void setToCache(Text<Y> computed);

}
