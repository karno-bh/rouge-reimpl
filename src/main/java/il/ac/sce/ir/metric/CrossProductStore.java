package il.ac.sce.ir.metric;

public interface CrossProductStore {

    <T> void getData(Tuple2<String, String> crossProductKey, CrossProductValue<T> collector);

    <T> void setData(Tuple2<String, String> crossProductKey, CrossProductValue<T> data);

}
