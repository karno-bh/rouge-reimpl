package il.ac.sce.ir.metric.concrete_metric.common.util;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import il.ac.sce.ir.metric.concrete_metric.common.nlp.processor.CoreNLPTextProcessor;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.container.data.ContainerConfigData;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.CacheMemoryTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileToStringProcessor;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.utils.file_system.CategoryPathResolver;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class SequentialPreCache {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void preCache(Configuration configuration) {
        String coreNLPPipelineAnnotators = "tokenize, ssplit, pos, lemma, truecase, ner, parse";
        Properties coreNLPPipeProperties = new Properties();
        coreNLPPipeProperties.setProperty("annotators", coreNLPPipelineAnnotators);
        Map<String, String> textIdCache = new ConcurrentHashMap<>();
        ContainerConfigData containerConfigData = fetchContainerData(configuration);
        String fileSystemCachePath = containerConfigData.getFileSystemCachePath();
        TextPipelineExtractor<String, String> cachedTextExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, Annotation> fsCachedCoreNLPAnnotationExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                // .cacheIn(CacheMemoryTextProcessor::new)
                .extract(cachedTextExtractor)
                .pipe(new CoreNLPTextProcessor(() -> new StanfordCoreNLP(coreNLPPipeProperties)))
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePath, "core-nlp-annotations", textIdCache))
                .extract(fsCachedCoreNLPAnnotationExtractor);

        FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();
        String workingSetDirectory = configuration.getWorkingSetDirectory();
        CategoryPathResolver categoryPathResolver = new CategoryPathResolver();
        List<ProcessedCategory> processedCategories = categoryPathResolver.resolveCategories(workingSetDirectory);
        FileSystemPath fileSystemPath = new FileSystemPath();
        for (ProcessedCategory processedCategory : processedCategories) {
            File topicsDir = fileSystemTopologyResolver.getTopicsDir(workingSetDirectory, processedCategory);
            List<String> allTopicFiles = fileSystemTopologyResolver.getAllTopicRelativeFiles(workingSetDirectory, processedCategory);
            double topicNum = 0;
            for (String topicFile : allTopicFiles) {
                String absoluteTopicPath = fileSystemPath.combinePath(topicsDir.getAbsolutePath(), topicFile);
                logger.info("So far processed {}% of topics, processing topic {}", (topicNum++) * 100d / (double) allTopicFiles.size(), absoluteTopicPath);
                Text<String> topic = Text.asFileLocation(absoluteTopicPath);
                fsCachedCoreNLPAnnotationExtractor.getTextProcessor().process(topic);
                System.gc();
                System.gc();
                System.gc();
                System.gc();
                System.gc();
            }
        }
    }

    protected ContainerConfigData fetchContainerData(Configuration configuration) {
        return ContainerConfigData.fromMap(configuration.getAdditionalContainerConfig());
    }
}
