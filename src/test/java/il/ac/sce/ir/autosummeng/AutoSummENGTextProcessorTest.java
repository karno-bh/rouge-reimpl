package il.ac.sce.ir.autosummeng;

import gr.demokritos.iit.jinsect.documentModel.documentTypes.NGramSymWinDocument;
import gr.demokritos.iit.jinsect.documentModel.documentTypes.SimpleTextDocument;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor.JInsectNGramSymWinDocumentTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.processor.JInsectSimpleTextDocumentTextProcessor;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.DocumentDesc;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.CacheMemoryTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import org.junit.Test;

public class AutoSummENGTextProcessorTest {

    private static final String fileSystemCachePrefix = "c:/my/temp/cache";

    private static final String jinsectSimpleTextDocumentCacheCategory = "jinsect-simple-text-document";
    private static final String jinsectNGRamSymWinDocumentCacheCategory = "jinsect-ngram-sym-win-document";

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
                new JInsectSimpleTextDocumentTextProcessor();

        TextPipelineExtractor<DocumentDesc, SimpleTextDocument> simpleTextDocumentTextPipelineExtractor = new TextPipelineExtractor<>();

        new TextPipeline<>(jInsectSimpleTextDocumentTextProcessor)
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePrefix, jinsectSimpleTextDocumentCacheCategory))
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(simpleTextDocumentTextPipelineExtractor);

        JInsectNGramSymWinDocumentTextProcessor jInsectNGramSymWinDocumentTextProcessor =
                new JInsectNGramSymWinDocumentTextProcessor();

        TextPipelineExtractor<DocumentDesc, NGramSymWinDocument> nGramSymWinDocumentTextPipelineExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(jInsectNGramSymWinDocumentTextProcessor)
                .cacheIn(processor ->  new FileSystemCacheTextProcessor<>(processor, fileSystemCachePrefix, jinsectNGRamSymWinDocumentCacheCategory))
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(nGramSymWinDocumentTextPipelineExtractor);

        DocumentDesc.TextBuilder builder = new DocumentDesc.TextBuilder();

        Text<DocumentDesc> fileLocationSimpleTextDocument = builder.buildText(fileName, wordMin, wordMax, wordDist);
        Text<DocumentDesc> fileLocationnGramSymWinDocument = builder.buildText(fileName, charMin, charMax, charDist);

        System.out.println(simpleTextDocumentTextPipelineExtractor.getTextProcessor().process(fileLocationSimpleTextDocument).getTextData());
        System.out.println(simpleTextDocumentTextPipelineExtractor.getTextProcessor().process(fileLocationSimpleTextDocument).getTextData());

        System.out.println(nGramSymWinDocumentTextPipelineExtractor.getTextProcessor().process(fileLocationnGramSymWinDocument).getTextData());
        System.out.println(nGramSymWinDocumentTextPipelineExtractor.getTextProcessor().process(fileLocationnGramSymWinDocument).getTextData());
    }
}
