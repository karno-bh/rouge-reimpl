package il.ac.sce.ir.metric.concrete_metric.rouge.processor;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SGramTextProcessor implements TextProcessor<List<String>, Map<String, Integer>> {

    public static final String COUNT = "_cn_";

    private final boolean useUnigrams;

    public SGramTextProcessor(boolean useUnigrams) {
        this.useUnigrams = useUnigrams;
    }

    @Override
    public Text<Map<String, Integer>> process(Text<List<String>> data) {
        List<String> tokens = data.getTextData();
        Map<String, Integer> skipBiGrams = new HashMap<>();
        if (tokens == null || tokens.isEmpty()) {
            skipBiGrams.put(COUNT, 0);
            return new Text<>(data.getTextId(), skipBiGrams);
        }
        if (!(tokens instanceof ArrayList)) {
            tokens = new ArrayList<>(tokens);
        }
        Map<String, int[]> realSkipBiGrams = new HashMap<>();
        int count = 0;
        for (int i = 0; i < tokens.size(); i++) {
            String leftToken = tokens.get(i);
            if (useUnigrams) {
                ++realSkipBiGrams.computeIfAbsent(leftToken, k -> new int[]{0})[0];
                ++count;
            }
            for (int j = i + 1; j < tokens.size(); j++) {
                String biGram = leftToken + " " + tokens.get(j);
                ++realSkipBiGrams.computeIfAbsent(biGram, k -> new int[]{0})[0];
                ++count;
            }
        }

        skipBiGrams.put(COUNT, count);
        realSkipBiGrams.forEach((k, v) -> skipBiGrams.put(k, v[0]));

        return new Text<>(data.getTextId(), skipBiGrams);
    }
}
