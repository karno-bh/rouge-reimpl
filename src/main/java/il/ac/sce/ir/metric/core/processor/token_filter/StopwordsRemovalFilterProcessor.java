package il.ac.sce.ir.metric.core.processor.token_filter;

import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.TextProcessor;

import java.util.*;

public class StopwordsRemovalFilterProcessor implements TextProcessor<List<String>, List<String>> {

    // took from: https://gist.github.com/sebleier/554280
    public final static String STOP_WORDS = "i me my myself we our ours ourselves you your yours yourself yourselves " +
            "he him his himself she her hers herself it its itself they them their theirs themselves what which who " +
            "whom this that these those am is are was were be been being have has had having do does did doing a an " +
            "the and but if or because as until while of at by for with about against between into through during " +
            "before after above below to from up down in out on off over under again further then once here there " +
            "when where why how all any both each few more most other some such no nor not only own same so than too " +
            "very s t can will just don should now";

    private final Set<String> stopWords;

    public StopwordsRemovalFilterProcessor() {
        stopWords = new HashSet<>(Arrays.asList(STOP_WORDS.split("\\s+")));
    }

    @Override
    public Text<List<String>> process(Text<List<String>> data) {
        List<String> filteredTokens = new ArrayList<>();


        for (String token : data.getTextData()) {
            if (!stopWords.contains(token)) {
                filteredTokens.add(token);
            }
        }

        return new Text<>(data.getTextId(), filteredTokens);
    }
}
