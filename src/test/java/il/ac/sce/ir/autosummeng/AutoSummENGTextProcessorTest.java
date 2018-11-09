package il.ac.sce.ir.autosummeng;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor.JInsectNGramSymWinDocumentTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor.JInsectSimpleTextDocumentTextProcessor;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.CacheMemoryTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import org.junit.Test;

import java.text.MessageFormat;

public class AutoSummENGTextProcessorTest {

    private static final String fileSystemCachePrefix = "c:/my/temp/cache";

    private static final String jinsectSimpleTextDocumentCacheCategory = "jinsect-simple-text-document";
    private static final String jinsectNGRamSymWinDocumentCacheCategory = "jinsect-ngram-sym-win-document";

    @Test
    @Deprecated
    /**
     * Look at test [autoSummEngWithTextProcessorWithSpecialTextTest] that uses textId to restore correct text
     */
    public void autoSummEngWithTextProcessorTest() {

        int wordMin = 1;
        int wordMax = 10;
        int wordDist = 5;
        int charMin = 1;
        int charMax = 10;
        int charDist = 5;

        final String fileName = "c:/my/temp/sample.txt";

        JInsectSimpleTextDocumentTextProcessor jInsectSimpleTextDocumentTextProcessor =
                new JInsectSimpleTextDocumentTextProcessor(wordMin, wordMax, wordDist);

        TextPipelineExtractor<String, SimpleTextDocument> simpleTextDocumentTextPipelineExtractor = new TextPipelineExtractor<>();

        new TextPipeline<>(jInsectSimpleTextDocumentTextProcessor)
                .cacheIn(processor -> {
                    JInsectSimpleTextDocumentTextProcessor textProcessor = (JInsectSimpleTextDocumentTextProcessor) processor;
                    final String fileCache = MessageFormat.format("{0}_{1}_{2}_{3}", jinsectSimpleTextDocumentCacheCategory,
                            textProcessor.getWordMin(), textProcessor.getWordMax(), textProcessor.getWordDist());
                    return new FileSystemCacheTextProcessor<>(processor, fileSystemCachePrefix, fileCache);
                })
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(simpleTextDocumentTextPipelineExtractor);

        JInsectNGramSymWinDocumentTextProcessor jInsectNGramSymWinDocumentTextProcessor =
                new JInsectNGramSymWinDocumentTextProcessor(charMin, charMax, charDist);

        TextPipelineExtractor<String, NGramSymWinDocument> nGramSymWinDocumentTextPipelineExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(jInsectNGramSymWinDocumentTextProcessor)
                .cacheIn(processor ->  new FileSystemCacheTextProcessor<>(processor, fileSystemCachePrefix, jinsectNGRamSymWinDocumentCacheCategory))
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(nGramSymWinDocumentTextPipelineExtractor);

        Text<String> fileLocation = Text.asFileLocation(fileName);

        System.out.println(simpleTextDocumentTextPipelineExtractor.getTextProcessor().process(fileLocation).getTextData());
        System.out.println(simpleTextDocumentTextPipelineExtractor.getTextProcessor().process(fileLocation).getTextData());

        System.out.println(nGramSymWinDocumentTextPipelineExtractor.getTextProcessor().process(fileLocation).getTextData());
        System.out.println(nGramSymWinDocumentTextPipelineExtractor.getTextProcessor().process(fileLocation).getTextData());
    }

    @Test
    public void autoSummEngWithTextProcessorWithSpecialTextTest() {

        int wordMin = 1;
        int wordMax = 10;
        int wordDist = 5;
        int charMin = 1;
        int charMax = 10;
        int charDist = 5;

        final String fileName = "c:/my/temp/sample.txt";

        JInsectSimpleTextDocumentTextProcessor jInsectSimpleTextDocumentTextProcessor =
                new JInsectSimpleTextDocumentTextProcessor(wordMin, wordMax, wordDist);

        TextPipelineExtractor<String, SimpleTextDocument> simpleTextDocumentTextPipelineExtractor = new TextPipelineExtractor<>();

        new TextPipeline<>(jInsectSimpleTextDocumentTextProcessor)
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePrefix, jinsectSimpleTextDocumentCacheCategory))
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(simpleTextDocumentTextPipelineExtractor);

        JInsectNGramSymWinDocumentTextProcessor jInsectNGramSymWinDocumentTextProcessor =
                new JInsectNGramSymWinDocumentTextProcessor(charMin, charMax, charDist);

        TextPipelineExtractor<String, NGramSymWinDocument> nGramSymWinDocumentTextPipelineExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(jInsectNGramSymWinDocumentTextProcessor)
                .cacheIn(processor ->  new FileSystemCacheTextProcessor<>(processor, fileSystemCachePrefix, jinsectNGRamSymWinDocumentCacheCategory))
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(nGramSymWinDocumentTextPipelineExtractor);

        Text<String> fileLocationSimpleTextDocument = new Text<>(
                MessageFormat.format("{0}__{1}_{2}_{3}", fileName, wordMin, wordMax, wordDist),
                fileName
        );

        Text<String> fileLocationnGramSymWinDocument = new Text<>(
                MessageFormat.format("{0}__{1}_{2}_{3}", fileName, charMin, charMax, charDist),
                fileName
        );

        System.out.println(simpleTextDocumentTextPipelineExtractor.getTextProcessor().process(fileLocationSimpleTextDocument).getTextData());
        System.out.println(simpleTextDocumentTextPipelineExtractor.getTextProcessor().process(fileLocationSimpleTextDocument).getTextData());

        System.out.println(nGramSymWinDocumentTextPipelineExtractor.getTextProcessor().process(fileLocationnGramSymWinDocument).getTextData());
        System.out.println(nGramSymWinDocumentTextPipelineExtractor.getTextProcessor().process(fileLocationnGramSymWinDocument).getTextData());
    }
}
