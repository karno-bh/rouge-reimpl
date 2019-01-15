package il.ac.sce.ir;

import il.ac.sce.ir.metric.concrete_metric.rouge.processor.NGramHits;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.NGramTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.SGramTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.score.Score;
import il.ac.sce.ir.metric.concrete_metric.rouge.score_calculator.RougeNMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.BiTextProcessor;
import il.ac.sce.ir.metric.core.processor.CacheMemoryTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileToStringProcessor;
import il.ac.sce.ir.metric.core.processor.TextToTokensProcessor;
import il.ac.sce.ir.metric.core.score_calculator.data.PeerMultiModelPair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RougeSPipeLineTest {

    @Test
    public void rougeSTokensTest() {
        List<Text<String>> models = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String modelId = "c:/my/temp/model" + i + ".txt";
            Text<String> model = new Text<>(modelId, modelId);
            models.add(model);
        }
        List<Text<String>> peers = new ArrayList<>();
        String peer1Id = "c:/my/temp/peer1.txt";
        Text<String> peer1 = new Text<>(peer1Id, peer1Id);
        peers.add(peer1);

        TextPipelineExtractor<String, List<String>> tokensExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, Map<String, Integer>> sGram1Extractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                .cacheIn((processor) -> new CacheMemoryTextProcessor<>(processor))
                .extract(tokensExtractor)
                .pipe(new SGramTextProcessor(true))
                .extract(sGram1Extractor);

        System.out.println("SGram: " + sGram1Extractor.getTextProcessor().process(peer1).getTextData());

    }

    @Test
    public void rougeSHits() {
        List<Text<String>> models = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            String modelId = "c:/my/temp/model" + i + ".txt";
            Text<String> model = new Text<>(modelId, modelId);
            models.add(model);
        }
        List<Text<String>> peers = new ArrayList<>();
        String peer1Id = "c:/my/temp/peer1.txt";
        Text<String> peer1 = new Text<>(peer1Id, peer1Id);
        peers.add(peer1);

        TextPipelineExtractor<String, List<String>> tokensExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, Map<String, Integer>> sGram1Extractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                .cacheIn((processor) -> new CacheMemoryTextProcessor<>(processor))
                .extract(tokensExtractor)
                .pipe(new SGramTextProcessor(true))
                .extract(sGram1Extractor);

        BiTextProcessor<String, Integer> nGramHits = new NGramHits(sGram1Extractor.getTextProcessor());
        RougeNMultimodelScoreCalculator rougeNMultimodelReporter = new RougeNMultimodelScoreCalculator();

        rougeNMultimodelReporter.setnGramProcessor(sGram1Extractor.getTextProcessor());
        rougeNMultimodelReporter.setnGramHits(nGramHits);

        PeerMultiModelPair peerMultiModelPair = new PeerMultiModelPair(peer1, models);


        Score s = rougeNMultimodelReporter.computeScore(peerMultiModelPair);
        System.out.println(s);

    }
}
