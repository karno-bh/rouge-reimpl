package il.ac.sce.ir.metric.core.container.container_algorithm;

import il.ac.sce.ir.metric.core.container.Container;

public interface MainAlgo extends Runnable {

    void setContainer(Container container);
    Container getContainer();
}
