package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.Objects;

public class PipelineProcessor<X, Y, Z> implements TextProcessor<X, Z> {

    private final TextProcessor<X, Y> first;
    private final TextProcessor<Y, Z> second;

    public PipelineProcessor(TextProcessor<X, Y> first, TextProcessor<Y, Z> second) {
        Objects.requireNonNull(first, "First processor cannot be null");
        Objects.requireNonNull(first, "Second processor cannot be null");
        this.first = first;
        this.second = second;
    }

    @Override
    public Text<Z> process(Text<X> data) {
        Text<Y> firstProcessed = first.process(data);
        return second.process(firstProcessed);
    }
}
