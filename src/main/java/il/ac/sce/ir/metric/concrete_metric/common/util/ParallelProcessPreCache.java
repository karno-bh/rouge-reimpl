package il.ac.sce.ir.metric.concrete_metric.common.util;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import il.ac.sce.ir.metric.concrete_metric.common.nlp.processor.CoreNLPTextProcessor;
import il.ac.sce.ir.metric.core.builder.TextPipeline;
import il.ac.sce.ir.metric.core.builder.TextPipelineExtractor;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.container.data.ContainerConfigData;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.processor.FileSystemCacheTextProcessor;
import il.ac.sce.ir.metric.core.processor.FileToStringProcessor;
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.utils.file_system.CategoryPathResolver;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelProcessPreCache {

    private final static Logger logger = LoggerFactory.getLogger(ParallelProcessPreCache.class);

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
        int threadsNum = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadsNum);
        for (ProcessedCategory processedCategory : processedCategories) {
            File topicsDir = fileSystemTopologyResolver.getTopicsDir(workingSetDirectory, processedCategory);
            List<String> allTopicFiles = fileSystemTopologyResolver.getAllTopicRelativeFiles(workingSetDirectory, processedCategory);
            Map<Integer, List<String>> chunks = getChunks(allTopicFiles, threadsNum);
            List<Future<?>> chunksFuture = new ArrayList<>();
            chunks.forEach((key, chunk) -> {
                WorkingThread workingThread = new WorkingThread(chunk, topicsDir, fsCachedCoreNLPAnnotationExtractor.getTextProcessor());
                chunksFuture.add(executorService.submit(workingThread));
            });
            chunksFuture.forEach(f -> {
                try {
                    f.get();
                } catch (Exception ignored) {}
            });
            executorService.shutdown();
        }
    }

    protected ContainerConfigData fetchContainerData(Configuration configuration) {
        return ContainerConfigData.fromMap(configuration.getAdditionalContainerConfig());
    }

    private Map<Integer, List<String>> getChunks(List<String> list, int partitions) {
        Map<Integer, List<String>> result = new HashMap<>();
        int i = 0;
        for (String el : list) {
            int partition = i++ % partitions;
            List<String> strings = result.computeIfAbsent(partition, key -> new ArrayList<>());
            strings.add(el);
        }
        return result;
    }


    private static class WorkingThread implements Runnable {

        private final List<String> chunk;

        private final File topicDir;

        private final TextProcessor<String, Annotation> textProcessor;

        public WorkingThread(List<String> chunk, File topicDir, TextProcessor<String, Annotation> textProcessor) {
            this.chunk = chunk;
            this.topicDir = topicDir;
            this.textProcessor = textProcessor;
        }

        @Override
        public void run() {
            FileSystemPath fileSystemPath = new FileSystemPath();
            for (String topicFile : chunk) {
                String absoluteTopicPath = fileSystemPath.combinePath(topicDir.getAbsolutePath(), topicFile);
                logger.info("processing topic {}", absoluteTopicPath);
                Text<String> topic = Text.asFileLocation(absoluteTopicPath);
                textProcessor.process(topic);
                System.gc();
                System.gc();
                System.gc();
                System.gc();
                System.gc();
            }
        }
    }
}
