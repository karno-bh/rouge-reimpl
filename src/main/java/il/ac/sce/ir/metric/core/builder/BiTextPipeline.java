package il.ac.sce.ir.metric.core.builder;

import il.ac.sce.ir.metric.core.processor.BiTextPipelineProcessor;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;
import il.ac.sce.ir.metric.core.processor.AbstractCacheBiTextProcessor;
import il.ac.sce.ir.metric.core.processor.BiTextContinuation;

import java.util.Objects;
import java.util.function.Function;

public class BiTextPipeline<X, Y> {

    private final BiTextProcessor<X, Y> current;

    public BiTextPipeline(BiTextProcessor<X, Y> biTextProcessor) {
        Objects.requireNonNull(biTextProcessor, "Bi Text Processor cannot be null");
        this.current = biTextProcessor;
    }

    public <Z> BiTextPipeline<X, Z> pipe(BiTextContinuation<Y, Z> continuation) {
        BiTextPipelineProcessor<X, Y, Z> pipedProcessor = new BiTextPipelineProcessor<>(current, continuation);
        return new BiTextPipeline<>(pipedProcessor);
    }

    public BiTextPipeline<X, Y> extract(BiTextPipelineExtractor<X, Y> extractor) {
        extractor.setBiTextProcessor(current);
        return this;
    }

    public BiTextPipeline<X, Y> cacheIn(Function<BiTextProcessor<X, Y>, AbstractCacheBiTextProcessor<X, Y>> cacheProducer) {
        AbstractCacheBiTextProcessor<X, Y> cache = cacheProducer.apply(current);
        return new BiTextPipeline<>(cache);
    }

    
}
