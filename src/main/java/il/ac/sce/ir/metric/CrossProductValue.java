package il.ac.sce.ir.metric;

import java.util.Objects;

public class CrossProductValue<T>{

    private T dotProductValue;

    private final Class<T> tClass;

    public CrossProductValue(Class<T> klass) {
        tClass = klass;
    }

    public void setDotProductValue(T dotProductValue) {
        Objects.requireNonNull(dotProductValue);
        if (dotProductValue.getClass() != tClass) {
            throw new ClassCastException("Cannot cast stored value to provided type");
        }
        this.dotProductValue = dotProductValue;
    }

    public T getDotProductValue() {
        return dotProductValue;
    }
}
