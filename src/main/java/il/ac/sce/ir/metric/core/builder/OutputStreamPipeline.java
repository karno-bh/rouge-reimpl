package il.ac.sce.ir.metric.core.builder;

import il.ac.sce.ir.metric.core.function.IOFunction;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class OutputStreamPipeline<X extends OutputStream> {

    private final X current;

    public OutputStreamPipeline(X current) {
        Objects.requireNonNull(current, "Output Stream cannot be null");
        this.current = current;
    }

    public <Y extends OutputStream> OutputStreamPipeline<Y> pipe(IOFunction<X, Y> nextSupplier) throws IOException {
        return new OutputStreamPipeline<>(nextSupplier.apply(current));
    }

    public X build() {
        return current;
    }
}
