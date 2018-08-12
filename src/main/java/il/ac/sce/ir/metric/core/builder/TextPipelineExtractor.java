package il.ac.sce.ir.metric.core.builder;

import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.Objects;

public class TextPipelineExtractor<X, Y> {

    private TextProcessor<X, Y> textProcessor;

    public TextProcessor<X, Y> getTextProcessor() {
        return textProcessor;
    }

    public void setTextProcessor(TextProcessor<X, Y> textProcessor) {
        Objects.requireNonNull(textProcessor);
        this.textProcessor = textProcessor;
    }
}
