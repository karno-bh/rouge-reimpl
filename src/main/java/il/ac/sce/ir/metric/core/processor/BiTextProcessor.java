package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.data.BiText;

public interface BiTextProcessor<X, Y> {

    BiText<Y> process(Text<X> left, Text<X> right);

}
