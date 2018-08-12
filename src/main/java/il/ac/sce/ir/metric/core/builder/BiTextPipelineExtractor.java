package il.ac.sce.ir.metric.core.builder;

import il.ac.sce.ir.metric.core.processor.BiTextProcessor;

import java.util.Objects;

public class BiTextPipelineExtractor<X, Y> {

    private BiTextProcessor<X, Y> biTextProcessor;

    public BiTextProcessor<X, Y> getBiTextProcessor() {
        return biTextProcessor;
    }

    public void setBiTextProcessor(BiTextProcessor<X, Y> biTextProcessor) {
        Objects.requireNonNull(biTextProcessor, "Bi Text Processor cannot be null");
        this.biTextProcessor = biTextProcessor;
    }
}
