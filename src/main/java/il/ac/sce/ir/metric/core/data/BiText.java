package il.ac.sce.ir.metric.core.data;

import java.util.Objects;

public class BiText<T> {

    private final String leftId;
    private final String rightId;

    private final T data;

    public BiText(String left, String right, T data) {
        Objects.requireNonNull(left, "Left text id cannot be null");
        Objects.requireNonNull(right, "Right text id cannot be null");
        Objects.requireNonNull(data, "Data cannot be null");
        this.leftId = left;
        this.rightId = right;
        this.data = data;
    }

    public String getLeftId() {
        return leftId;
    }

    public String getRightId() {
        return rightId;
    }

    public T getData() {
        return data;
    }
}
