package il.ac.sce.ir.metric.core.reducer;

import java.util.ArrayList;
import java.util.List;

public class CombineReducer implements Reducer{

    private final List<Reducer> reducers = new ArrayList<>();

    public void addReducer(Reducer reducer) {
        reducers.add(reducer);
    }

    @Override
    public void reduce() {
        reducers.forEach(Reducer::reduce);
    }
}
