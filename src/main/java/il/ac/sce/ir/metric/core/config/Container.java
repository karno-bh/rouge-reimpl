package il.ac.sce.ir.metric.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Container {

    private final Map<String, Object> container = new ConcurrentHashMap<>();

    public Object getBean(String beanKey) {
        return container.get(beanKey);
    }

    protected void setBean(String beanKey, Object bean) {
        container.put(beanKey, bean);
    }

    public abstract void build();
    public abstract void setConfiguration(Configuration configureation);
    public abstract Configuration getConfiguration();
}
