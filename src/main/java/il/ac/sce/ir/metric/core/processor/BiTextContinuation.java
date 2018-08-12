package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.BiText;

public interface BiTextContinuation<X, Y> {

    BiText<Y> process(BiText<X> biText);
}
