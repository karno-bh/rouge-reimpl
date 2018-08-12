package il.ac.sce.ir.metric;

import java.util.Objects;

public final class Tuple2<L,R> {

    private final L left;
    private final R right;

    public Tuple2(L left, R right) {
        Objects.requireNonNull(left, "Left tuple member cannot be null");
        Objects.requireNonNull(right, "Right tuple member cannot be null");
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        int hash = 13;
        hash = 31 * hash + (left  == null ? 0 :  left.hashCode());
        hash = 31 * hash + (right == null ? 0 : right.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj != null && obj instanceof Tuple2) {
            Tuple2 other = (Tuple2) obj;
            return left.equals(other.left) && right.equals(other.right);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Left: " + left + " ;Right: " + right;
    }
}
