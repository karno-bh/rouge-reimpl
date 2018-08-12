package il.ac.sce.ir.metric.rouge.processor;

import il.ac.sce.ir.metric.Document;
import il.ac.sce.ir.metric.DocumentPipeline;
import il.ac.sce.ir.metric.NGramExtractor;

import java.util.List;
import java.util.Map;

public class NGramPipeline implements DocumentPipeline {

    private NGramExtractor nGramExtractor;
    private int nSize = 1;

    @Override
    public void process(Document document) {
        List<String> tokens = document.getTokens();
        Map<String, Integer> nGrams = nGramExtractor.extract(tokens, nSize);
        document.setnGram(nGrams, nSize);
    }

    public void setnGramExtractor(NGramExtractor nGramExtractor) {
        this.nGramExtractor = nGramExtractor;
    }

    public void setnSize(int nSize) {
        this.nSize = nSize;
    }
}
