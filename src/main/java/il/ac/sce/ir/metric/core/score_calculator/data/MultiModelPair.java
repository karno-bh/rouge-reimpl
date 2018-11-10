package il.ac.sce.ir.metric.core.score_calculator.data;

import il.ac.sce.ir.metric.core.data.Text;

import java.util.List;

public class MultiModelPair {

    private final Text<String> peer;

    private final List<Text<String>> models;

    public MultiModelPair(Text<String> peer, List<Text<String>> models) {
        this.peer = peer;
        this.models = models;
    }

    public Text<String> getPeer() {
        return peer;
    }

    public List<Text<String>> getModels() {
        return models;
    }
}
