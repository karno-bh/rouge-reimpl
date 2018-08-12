package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.data.BiText;

import java.util.Objects;

public class BiTextPipelineProcessor<X, Y, Z> implements BiTextProcessor<X, Z> {

    private final BiTextProcessor<X, Y> firstProcessor;

    private final BiTextContinuation<Y, Z> second;

    public BiTextPipelineProcessor(BiTextProcessor<X, Y> firstProcessor, BiTextContinuation<Y, Z> second) {
        Objects.requireNonNull(firstProcessor, "First processor cannot be null");
        Objects.requireNonNull(second, "Continuation cannot be null");
        this.firstProcessor = firstProcessor;
        this.second = second;
    }


    @Override
    public BiText<Z> process(Text<X> left, Text<X> right) {
        BiText<Y> firstResult = firstProcessor.process(left, right);
        BiText<Z> result = second.process(firstResult);
        return result;
    }
}
