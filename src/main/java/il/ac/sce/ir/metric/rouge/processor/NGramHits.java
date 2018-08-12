package il.ac.sce.ir.metric.rouge.processor;

import il.ac.sce.ir.metric.rouge.utils.RougeUtils;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;

import java.util.Map;
import java.util.Objects;

public class NGramHits implements BiTextProcessor<String, Integer> {

    private final TextProcessor<String, Map<String, Integer>> textToNGramProcessor;

    public NGramHits(TextProcessor<String, Map<String, Integer>> textToNGramProcessor) {
        Objects.requireNonNull(textToNGramProcessor, "Text to NGram processor cannot be null");
        this.textToNGramProcessor = textToNGramProcessor;
    }

    @Override
    public BiText<Integer> process(Text<String> left, Text<String> right) {
        Text<Map<String, Integer>> leftResult = textToNGramProcessor.process(left);
        Text<Map<String, Integer>> rightResult = textToNGramProcessor.process(right);
        int nGramHits = RougeUtils.nGramHits(leftResult.getTextData(), rightResult.getTextData());

        return new BiText<>(left.getTextId(), right.getTextId(), nGramHits);
    }
}
