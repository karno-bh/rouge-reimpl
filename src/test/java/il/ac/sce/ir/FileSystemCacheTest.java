package il.ac.sce.ir;

import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileToStringProcessor;
import il.ac.sce.ir.metric.core.processor.TextToTokensProcessor;
import il.ac.sce.ir.metric.rouge.processor.NGramTextProcessor;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileSystemCacheTest {

    @Test
    public void fileSystemCacheTest() {
        String peer1Id = "c:/my/temp/peer1.txt";
        String fsCachePrefix = "c:/my/temp/cache";
        Map<String, String> textIdCache = new ConcurrentHashMap<>();
        TextPipelineExtractor<String, List<String>> tokensExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, Map<String, Integer>> nGram1Extractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                .pipe(new TextToTokensProcessor())
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fsCachePrefix,  "text-tokens", textIdCache))
                .extract(tokensExtractor)
                .pipe(new NGramTextProcessor(1))
                //.cacheIn(CacheMemoryTextProcessor::new)
                .extract(nGram1Extractor);
        Text<String> text = new Text<>(peer1Id, peer1Id);
        System.out.println("First: " + nGram1Extractor.getTextProcessor().process(text).getTextData());
        System.out.println("Second: " + nGram1Extractor.getTextProcessor().process(text).getTextData());
    }
}
