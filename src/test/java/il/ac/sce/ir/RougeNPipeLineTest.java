package il.ac.sce.ir;

import il.ac.sce.ir.metric.core.builder.BiTextPipeline;
import il.ac.sce.ir.metric.core.builder.BiTextPipelineExtractor;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.BiText;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.*;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.DPMatrixBiTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.NGramHits;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.NGramTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.rouge.processor.SomeModelMatchContinuation;
import il.ac.sce.ir.metric.concrete_metric.rouge.score.RougeLMultimodelScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.rouge.score.RougeNMultimodelScoreCalculator;
import il.ac.sce.ir.metric.concrete_metric.rouge.score.RougeWMultimodelScoreCalculator;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RougeNPipeLineTest {

    @Test
    public void RougeNPipeLineTest() {

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
        TextPipelineExtractor<String, Map<String, Integer>> nGram1Extractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                .cacheIn((processor) -> new CacheMemoryTextProcessor<>(processor))
                .extract(tokensExtractor)
                .pipe(new NGramTextProcessor(1))
                .extract(nGram1Extractor);

        TextPipelineExtractor<String, Map<String, Integer>> nGram2Extractor = new TextPipelineExtractor<>();
        new TextPipeline<>(tokensExtractor.getTextProcessor())
                .pipe(new NGramTextProcessor(2))
                .extract(nGram2Extractor);

        BiTextProcessor<String, Integer> nGramHits = new NGramHits(nGram1Extractor.getTextProcessor());

        for (Text<String> model : models) {
            BiText<Integer> result = nGramHits.process(peer1, model);
            String print = MessageFormat.format("Peer: {0}, Model: {1}, NGramHits: {2}", result.getLeftId(), result.getRightId(), result.getData());
            System.out.println(print);
        }
    }


    @Test
    public void testRougeNReporter() {
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
        TextPipelineExtractor<String, Map<String, Integer>> nGram1Extractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(tokensExtractor)
                .pipe(new NGramTextProcessor(1))
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(nGram1Extractor);

        BiTextProcessor<String, Integer> nGramHits = new NGramHits(nGram1Extractor.getTextProcessor());

        RougeNMultimodelScoreCalculator rougeNMultimodelReporter = new RougeNMultimodelScoreCalculator();
        rougeNMultimodelReporter.setModels(models);
        rougeNMultimodelReporter.setPeer(peer1);
        rougeNMultimodelReporter.setnGramProcessor(nGram1Extractor.getTextProcessor());
        rougeNMultimodelReporter.setnGramHits(nGramHits);

        Score s = rougeNMultimodelReporter.computeScore();
        System.out.println(s);

        RougeLMultimodelScoreCalculator rougeLMultimodelReporter = new RougeLMultimodelScoreCalculator();
        rougeLMultimodelReporter.setModels(models);
        rougeLMultimodelReporter.setPeer(peer1);

        BiTextPipelineExtractor<List<String>, int[][]> dpMatrixExtractor = new BiTextPipelineExtractor<>();
        BiTextPipelineExtractor<List<String>, boolean[]> someModelMatchExtractor = new BiTextPipelineExtractor<>();
        new BiTextPipeline<>(new DPMatrixBiTextProcessor())
                .cacheIn(CacheMemoryBiTextProcessor::new)
                .extract(dpMatrixExtractor)
                .pipe(new SomeModelMatchContinuation())
                .extract(someModelMatchExtractor);
        //rougeLMultimodelReporter.setDpMatrixProcessor(new CacheMemoryBiTextProcessor<>(new DPMatrixBiTextProcessor()));
        rougeLMultimodelReporter.setDpMatrixProcessor(dpMatrixExtractor.getBiTextProcessor());
        rougeLMultimodelReporter.setTokensProcessor(tokensExtractor.getTextProcessor());

        s = rougeLMultimodelReporter.computeScore();
        System.out.println(s);

        RougeWMultimodelScoreCalculator rougeWMultimodelReporter = new RougeWMultimodelScoreCalculator();
        final double weighFactor = 1.2d;
        rougeWMultimodelReporter.setWeightFunctionSupplier(() -> value -> Math.pow(value, weighFactor));
        rougeWMultimodelReporter.setInverseWeightFunctionSupplier(() -> value -> Math.pow(value, 1 / weighFactor));
        rougeWMultimodelReporter.setModels(models);
        rougeWMultimodelReporter.setPeer(peer1);
        rougeWMultimodelReporter.setTokenProcessor(tokensExtractor.getTextProcessor());
        rougeWMultimodelReporter.setSomeModelMatchProcessor(someModelMatchExtractor.getBiTextProcessor());

        s = rougeWMultimodelReporter.computeScore();
        System.out.println(s);
    }
}
