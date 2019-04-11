package il.ac.sce.ir.metric.core.processor;

import il.ac.sce.ir.metric.core.data.Text;

public interface TextProcessor<X, Y> {

    Text<Y> process(Text<X> data);

}
