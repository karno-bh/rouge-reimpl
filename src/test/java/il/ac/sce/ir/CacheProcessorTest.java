package il.ac.sce.ir;

import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.*;
import il.ac.sce.ir.metric.rouge.processor.NGramTextProcessor;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class CacheProcessorTest {

    @Test
    public void memoryCacheTest() {
        TextProcessor<String, String> fileDocumentProcessor = new FileToStringProcessor();
        TextProcessor<String, List<String>> tokenProcessor = new TextToTokensProcessor();
        TextProcessor<String, List<String>> cacheTokenProcessor = new CacheMemoryTextProcessor<>(null, tokenProcessor);
        Text<String> initialData = new Text<>("c:/my/temp/doc1.txt", "c:/my/temp/doc1.txt");
        Text<String> text = fileDocumentProcessor.process(initialData);
        Text<List<String>> tokens = cacheTokenProcessor.process(text);
        System.out.println(tokens);
        Text<List<String>> tokens2 = cacheTokenProcessor.process(text);
        System.out.println(tokens2);
    }

    @Test
    public void memoryCacheAndPipelineTest() {
        TextProcessor<String, String> fileDocumentProcessor = new FileToStringProcessor();
        TextProcessor<String, List<String>> tokenProcessor = new TextToTokensProcessor();
        TextProcessor<String, List<String>> pipeline = new PipelineProcessor<>(fileDocumentProcessor, tokenProcessor);
        TextProcessor<String, List<String>> cacheTokenProcessor = new CacheMemoryTextProcessor<>(null, pipeline);

        Text<String> initialData = new Text<>("c:/my/temp/doc1.txt", "c:/my/temp/doc1.txt");
        //Text<String> text = fileDocumentProcessor.process(initialData);
        Text<List<String>> tokens = cacheTokenProcessor.process(initialData);
        System.out.println(tokens);
        Text<List<String>> tokens2 = cacheTokenProcessor.process(initialData);
        System.out.println(tokens2);
    }

    @Test
    public void nGramTextProcessorTest() {
        Text<String> initialData = new Text<>("c:/my/temp/doc1.txt", "c:/my/temp/doc1.txt");

        TextProcessor<String, String> fileDocumentProcessor = new FileToStringProcessor();
        TextProcessor<String, List<String>> tokenProcessor = new TextToTokensProcessor();
        TextProcessor<List<String>, Map<String, Integer>> nGramProcessor1 = new NGramTextProcessor(1);
        TextProcessor<List<String>, Map<String, Integer>> nGramProcessor2 = new NGramTextProcessor(2);
        TextProcessor<String, List<String>> pipeline01 = new PipelineProcessor<>(fileDocumentProcessor, tokenProcessor);
        TextProcessor<String, List<String>> memoryCachedPipeLine01 = new CacheMemoryTextProcessor<>(pipeline01);
        TextProcessor<String, Map<String, Integer>> nGram1 = new PipelineProcessor<>(memoryCachedPipeLine01, nGramProcessor1);
        TextProcessor<String, Map<String, Integer>> nGram2 = new PipelineProcessor<>(memoryCachedPipeLine01, nGramProcessor2);
        System.out.println("NGram1: " + nGram1.process(initialData).getTextData());
        System.out.println("NGram2: " + nGram2.process(initialData).getTextData());
    }

    @Test
    public void pipeFunctionTest() {
        Text<String> initialData = new Text<>("c:/my/temp/doc1.txt", "c:/my/temp/doc1.txt");

        TextProcessor<String, String> fileDocumentProcessor = new FileToStringProcessor();
        TextProcessor<String, List<String>> tokenProcessor = new TextToTokensProcessor();
        TextProcessor<List<String>, Map<String, Integer>> nGramProcessor1 = new NGramTextProcessor(1);
        TextProcessor<List<String>, Map<String, Integer>> nGramProcessor2 = new NGramTextProcessor(2);
        TextProcessor<String, List<String>> pipeline01 = new PipelineProcessor<>(fileDocumentProcessor, tokenProcessor);
        TextProcessor<String, List<String>> memoryCachedPipeLine01 = new CacheMemoryTextProcessor<>(pipeline01);
        TextProcessor<String, Map<String, Integer>> nGram1 = new PipelineProcessor<>(memoryCachedPipeLine01, nGramProcessor1);
        TextProcessor<String, Map<String, Integer>> nGram2 = new PipelineProcessor<>(memoryCachedPipeLine01, nGramProcessor2);



        System.out.println("NGram1: " + nGram1.process(initialData).getTextData());
        System.out.println("NGram2: " + nGram2.process(initialData).getTextData());
    }

    @Test
    public void textPipelineTest() {
        Text<String> initialData = new Text<>("c:/my/temp/doc1.txt", "c:/my/temp/doc1.txt");

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

        Text<Map<String, Integer>> nGram1Data = nGram1Extractor.getTextProcessor().process(initialData);
        System.out.println(nGram1Data.getTextData());
        Text<Map<String, Integer>> nGramData2 = nGram2Extractor.getTextProcessor().process(initialData);
        System.out.println(nGramData2.getTextData());
    }
}
