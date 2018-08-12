package il.ac.sce.ir.metric;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NgramExtractorDefaultImpl implements NGramExtractor {

    public Map<String, Integer> extract(List<String> tokens, int nSize) {
        Map<String, Integer> nGrams = new HashMap<>();
        if (tokens == null || tokens.isEmpty()) {
            nGrams.put(COUNT,0);
            return nGrams;
        }
        int nGramRange = tokens.size() - nSize + 1;
        for (int i = 0; i < nGramRange; i++) {
            List<String> nGramTokens = tokens.subList(i, i + nSize);
            String nGram = String.join(" ", nGramTokens);
            int nGramCount = nGrams.getOrDefault(nGram, 0);
            nGrams.put(nGram, ++nGramCount);
        }
        nGrams.put(COUNT, nGramRange); // number of elements in range
        return nGrams;
    }
}
