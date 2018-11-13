package il.ac.sce.ir.metric.concrete_metric.rouge.score;

import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;

import java.util.List;
import java.util.Map;

public class RougeNMultimodelScoreCalculator implements PeerMultimodelScoreCalculator {

    private BiTextProcessor<String, Integer> nGramHits;

    private TextProcessor<String, Map<String, Integer>> nGramProcessor;

    public void setnGramHits(BiTextProcessor<String, Integer> nGramHits) {
        this.nGramHits = nGramHits;
    }

    public void setnGramProcessor(TextProcessor<String, Map<String, Integer>> nGramProcessor) {
        this.nGramProcessor = nGramProcessor;
    }

    public BiTextProcessor<String, Integer> getnGramHits() {
        return nGramHits;
    }

    @Override
    public Score computeScore(MultiModelPair multiModelPair) {
        Text<String> peer = multiModelPair.getPeer();
        List<Text<String>> models = multiModelPair.getModels();

        double totalHits = 0;
        double totalModelGrams = 0;
        double totalPeerGrams = 0;
        for (Text<String> model : models) {
            Text<Map<String, Integer>> modelGrams = nGramProcessor.process(model);
            Text<Map<String, Integer>> peerGrams = nGramProcessor.process(peer);
            BiText<Integer> hits = nGramHits.process(model, peer);
            totalHits += hits.getData();
            totalModelGrams += modelGrams.getTextData().get("_cn_");
            totalPeerGrams += peerGrams.getTextData().get("_cn_");
        }
        Score score = new Score.Builder()
                .precision(totalHits / totalPeerGrams)
                .recall(totalHits / totalModelGrams)
                .build();
        return score;
    }
}
