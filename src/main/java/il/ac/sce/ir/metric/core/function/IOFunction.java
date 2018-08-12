package il.ac.sce.ir.metric.core.function;

import java.io.IOException;

public interface IOFunction<X, Y> {

    Y apply(X x) throws IOException;
}
