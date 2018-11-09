package il.ac.sce.ir;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.CacheMemoryTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileToStringProcessor;
import il.ac.sce.ir.metric.core.score.ReadabilityMetricScore;
import il.ac.sce.ir.metric.concrete_metric.common.nlp.processor.CoreNLPTextProcessor;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ReadabilityMetricScoreTest {

    @Test
    public void readabilityMetricScoreProcessorTest() {
        String peer1Id = "c:/my/temp/sample.txt";
        String fsCachePrefix = "c:/my/temp/cache";
        Map<String, String> textIdCache = new ConcurrentHashMap<>();

        Text<String> peer1 = new Text<>(peer1Id, peer1Id);
        String coreNLPPipelineAnnotators = "tokenize, ssplit, pos, lemma, truecase, ner, parse, dcoref";
        Properties coreNLPPipeProperties = new Properties();
        coreNLPPipeProperties.setProperty("annotators", coreNLPPipelineAnnotators);

        TextPipelineExtractor<String, String> cachedTextExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, Annotation> fsCachedCoreNLPAnnotationExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(cachedTextExtractor)
                .pipe(new CoreNLPTextProcessor(() -> new StanfordCoreNLP(coreNLPPipeProperties)))
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fsCachePrefix, "core-nlp-annotations", textIdCache))
                .extract(fsCachedCoreNLPAnnotationExtractor);

        ElenaReadabilityMetricScoreCalculator readabilityMetricScoreReporter = new ElenaReadabilityMetricScoreCalculator();
        readabilityMetricScoreReporter.setDocumentAnnotationProcessor(fsCachedCoreNLPAnnotationExtractor.getTextProcessor());
        readabilityMetricScoreReporter.setTextReadProcessor(cachedTextExtractor.getTextProcessor());
        readabilityMetricScoreReporter.setOriginalText(peer1);

        ReadabilityMetricScore readabilityMetricScore = readabilityMetricScoreReporter.computeScore();
        System.out.println("FleschReadingEase: " + readabilityMetricScore.getFleschReadingEase());
        System.out.println("ProperNounRation: " + readabilityMetricScore.getProperNounRation());
        System.out.println("UniqueProperNounsRatio: " + readabilityMetricScore.getUniqueProperNounsRatio());
        System.out.println("WordVariationIndex: " + readabilityMetricScore.getWordVariationIndex());
    }
}
