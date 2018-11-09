package il.ac.sce.ir;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import il.ac.sce.ir.metric.concrete_metric.common.nlp.processor.CoreNLPTextProcessor;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileToStringProcessor;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class TestCoreNLPPipeline {

    @Test
    public void coreNlpPipelineSaveTest() {
        String peer1Id = "c:/my/temp/peer1.txt";
        String fsCachePrefix = "c:/my/temp/cache";
        String coreNLPPipelineAnnotators = "tokenize, ssplit, pos, lemma, truecase, ner, parse, dcoref";
        Properties coreNLPPipeProperties = new Properties();
        coreNLPPipeProperties.setProperty("annotators", coreNLPPipelineAnnotators);

        Map<String, String> textIdCache = new ConcurrentHashMap<>();
        TextPipelineExtractor<String, Annotation> coreNLPExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .pipe(new CoreNLPTextProcessor(() -> new StanfordCoreNLP(coreNLPPipeProperties)))
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fsCachePrefix, "core-nlp-annotations", textIdCache))
                .extract(coreNLPExtractor);
        System.out.println(coreNLPExtractor.getTextProcessor().process(new Text<>(peer1Id, peer1Id)).getTextData());
        System.out.println(coreNLPExtractor.getTextProcessor().process(new Text<>(peer1Id, peer1Id)).getTextData());
    }
}
