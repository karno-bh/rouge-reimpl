package il.ac.sce.ir.metric.core.builder;

import il.ac.sce.ir.metric.core.processor.PipelineProcessor;
import il.ac.sce.ir.metric.core.processor.AbstractCacheTextProcessor;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.Objects;
import java.util.function.Function;

public class TextPipeline<X, Y> {

    private final TextProcessor<X, Y> current;

    public TextPipeline(TextProcessor<X, Y> textProcessor) {
        Objects.requireNonNull(textProcessor, "Text processor cannot be null");
        this.current = textProcessor;
    }

    public <Z> TextPipeline<X, Z> pipe(TextProcessor<Y, Z> next) {
        PipelineProcessor<X, Y, Z> pipedProcessors = new PipelineProcessor<>(current, next);
        return new TextPipeline<>(pipedProcessors);
    }

    public TextPipeline<X,Y> pipeIf(boolean condition, TextProcessor<Y, Y> next) {
        if (condition) {
            PipelineProcessor<X, Y, Y> pipedProcessors = new PipelineProcessor<>(current, next);
            return new TextPipeline<>(pipedProcessors);
        }
        return this;
    }

    public TextPipeline<X, Y> extract(TextPipelineExtractor<X, Y> extractor) {
        extractor.setTextProcessor(current);
        return this;
    }

    public TextPipeline<X, Y> cacheIn(Function<TextProcessor<X, Y>, AbstractCacheTextProcessor<X, Y>> cacheProducer) {
        AbstractCacheTextProcessor<X, Y> cache = cacheProducer.apply(current);
        return new TextPipeline<>(cache);
    }

    /*public TextPipeline<X, Y> cacheIn(AbstractCacheTextProcessor<X, Y> cache) {
        return null;
    }*/

}
