package il.ac.sce.ir.metric.concrete_metric.rouge.processor;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NGramTextProcessor implements TextProcessor<List<String>, Map<String, Integer>> {

    public static final String COUNT = "_cn_";;

    private final int nSize;

    public NGramTextProcessor(int nSize) {
        if (nSize <= 0) {
            throw new IllegalArgumentException("Size of nGram should be greater than zero");
        }
        this.nSize = nSize;
    }

    @Override
    public Text<Map<String, Integer>> process(Text<List<String>> data) {
        List<String> tokens = data.getTextData();
        Map<String, Integer> nGrams = new HashMap<>();
        if (tokens == null || tokens.isEmpty()) {
            nGrams.put(COUNT,0);
            return new Text<>(data.getTextId(), nGrams);
        }
        int nGramRange = tokens.size() - nSize + 1;
        for (int i = 0; i < nGramRange; i++) {
            List<String> nGramTokens = tokens.subList(i, i + nSize);
            String nGram = String.join(" ", nGramTokens);
            int nGramCount = nGrams.getOrDefault(nGram, 0);
            nGrams.put(nGram, ++nGramCount);
        }
        nGrams.put(COUNT, nGramRange); // number of elements in range
        return new Text<>(data.getTextId(), nGrams);
    }
}
