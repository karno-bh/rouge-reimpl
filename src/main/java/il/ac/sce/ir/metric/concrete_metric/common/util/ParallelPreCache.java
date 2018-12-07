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
import il.ac.sce.ir.metric.core.processor.TextProcessor;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.utils.file_system.CategoryPathResolver;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ParallelPreCache {

    private final static Logger logger = LoggerFactory.getLogger(ParallelPreCache.class);

    private TextProcessor<String, Annotation> textProcessor;

    private FileSystemCacheTextProcessor<String, Annotation> fileSystemCacheTextProcessor;

    private boolean topicsEnabled;

    private boolean peersEnabled;

    public TextProcessor<String, Annotation> getTextProcessor() {
        return textProcessor;
    }

    public void setTextProcessor(TextProcessor<String, Annotation> textProcessor) {
        this.textProcessor = textProcessor;
    }

    public boolean isTopicsEnabled() {
        return topicsEnabled;
    }

    public void setTopicsEnabled(boolean topicsEnabled) {
        this.topicsEnabled = topicsEnabled;
    }

    public boolean isPeersEnabled() {
        return peersEnabled;
    }

    public void setPeersEnabled(boolean peersEnabled) {
        this.peersEnabled = peersEnabled;
    }

    public FileSystemCacheTextProcessor<String, Annotation> getFileSystemCacheTextProcessor() {
        return fileSystemCacheTextProcessor;
    }

    public void setFileSystemCacheTextProcessor(FileSystemCacheTextProcessor<String, Annotation> fileSystemCacheTextProcessor) {
        this.fileSystemCacheTextProcessor = fileSystemCacheTextProcessor;
    }

    public void preCache(Configuration configuration) {
        if (!peersEnabled && !topicsEnabled) {
            return;
        }
        String coreNLPPipelineAnnotators = "tokenize, ssplit, pos, lemma, truecase, ner, parse";
        Properties coreNLPPipeProperties = new Properties();
        coreNLPPipeProperties.setProperty("annotators", coreNLPPipelineAnnotators);
        Map<String, String> textIdCache = new ConcurrentHashMap<>();
        ContainerConfigData containerConfigData = fetchContainerData(configuration);
        String fileSystemCachePath = containerConfigData.getFileSystemCachePath();
        /*TextPipelineExtractor<String, String> cachedTextExtractor = new TextPipelineExtractor<>();
        TextPipelineExtractor<String, Annotation> fsCachedCoreNLPAnnotationExtractor = new TextPipelineExtractor<>();
        new TextPipeline<>(new FileToStringProcessor())
                // .cacheIn(CacheMemoryTextProcessor::new)
                .extract(cachedTextExtractor)
                .pipe(new CoreNLPTextProcessor(() -> new StanfordCoreNLP(coreNLPPipeProperties)))
                .cacheIn(processor -> new FileSystemCacheTextProcessor<>(processor, fileSystemCachePath, "core-nlp-annotations", textIdCache))
                .cacheIn(CacheMemoryTextProcessor::new)
                .extract(fsCachedCoreNLPAnnotationExtractor);*/

        FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();
        String workingSetDirectory = configuration.getWorkingSetDirectory();
        CategoryPathResolver categoryPathResolver = new CategoryPathResolver();
        List<ProcessedCategory> processedCategories = categoryPathResolver.resolveCategories(workingSetDirectory);
        FileSystemPath fileSystemPath = new FileSystemPath();
        // the number of thread is chosen 3 because StanfordCoreNLP pipeline is about 1 gigabyte
        // Since the process is started with 4 GB, 3 threads with 3 StanfordCoreNLP pipeline is good enough
        int threadsNum = 3;
        ExecutorService executorServiceTopic = Executors.newFixedThreadPool(threadsNum);
        //TextProcessor<String, Annotation> textProcessor = fsCachedCoreNLPAnnotationExtractor.getTextProcessor();
        try {

            if (topicsEnabled) {
                for (ProcessedCategory processedCategory : processedCategories) {
                    File topicsDir = fileSystemTopologyResolver.getTopicsDir(workingSetDirectory, processedCategory);
                    List<String> allTopicFiles = fileSystemTopologyResolver.getAllTopicRelativeFiles(workingSetDirectory, processedCategory);
                    List<String> filesToProcess = allTopicFiles.stream().map(fileName -> fileSystemPath.combinePath(topicsDir.getAbsolutePath(), fileName)).collect(Collectors.toList());
                    // divToChunksAndProcess(executorServiceTopic, threadsNum, filesToProcess, textProcessor);
                    List<Future<Text<Annotation>>> futures = new ArrayList<>();
                    List<Text<Annotation>> textAnnotations = new ArrayList<>();
                    filesToProcess.forEach(file -> {
                        if (!fileSystemCacheTextProcessor.pickCached(file)) {
                            Future<Text<Annotation>> future = executorServiceTopic.submit(new WorkingChunk(file, textProcessor));
                            futures.add(future);
                        }
                    });
                    futures.forEach(future -> {
                        try {
                            Text<Annotation> annotationText = future.get();
                            textAnnotations.add(annotationText);
                        } catch (Exception ignore) {}
                    });
                    textAnnotations.forEach(fileSystemCacheTextProcessor::setToCache);
                }
            }

            if (peersEnabled) {
                for (ProcessedCategory processedCategory : processedCategories) {
                    File modelsDirectory = fileSystemTopologyResolver.getModelsDirectory(workingSetDirectory, processedCategory);
                    ArrayList<String> filesToProcess = new ArrayList<>();
                    List<ProcessedSystem> processedSystems = fileSystemTopologyResolver.getProcessedSystems(workingSetDirectory, processedCategory);
                    if (processedSystems == null || processedSystems.isEmpty()) {
                        logger.warn("Category {} does not have any system", processedCategory.getDirLocation());
                        return;
                    }
                    for (ProcessedSystem processedSystem : processedSystems) {
                        List<String> peerFileNames = fileSystemTopologyResolver.getPeerFileNames(processedSystem);
                        if (peerFileNames.isEmpty()) {
                            logger.warn("System {} does not have any file", processedSystem.getDescription());
                            continue;
                        }
                        for (String peerFileName : peerFileNames) {
                            // Text<String> peerText = Text.asFileLocation(fileSystemPath.combinePath(processedSystem.getDirLocation(), peerFileName));
                            filesToProcess.add(fileSystemPath.combinePath(processedSystem.getDirLocation(), peerFileName));
                        }
                    }
                    // divToChunksAndProcess(executorServiceTopic, threadsNum, filesToProcess, textProcessor);
                    List<Future<Text<Annotation>>> futures = new ArrayList<>();
                    List<Text<Annotation>> textAnnotations = new ArrayList<>();
                    filesToProcess.forEach(file -> {
                        if (!fileSystemCacheTextProcessor.pickCached(file)) {
                            Future<Text<Annotation>> future = executorServiceTopic.submit(new WorkingChunk(file, textProcessor));
                            futures.add(future);
                        }
                    });
                    futures.forEach(future -> {
                        try {
                            Text<Annotation> annotationText = future.get();
                            textAnnotations.add(annotationText);
                        } catch (Exception ignore) {}
                    });
                    textAnnotations.forEach(fileSystemCacheTextProcessor::setToCache);
                }
            }
        } finally {
            executorServiceTopic.shutdown();
        }
    }

    protected ContainerConfigData fetchContainerData(Configuration configuration) {
        return ContainerConfigData.fromMap(configuration.getAdditionalContainerConfig());
    }

    private static class WorkingChunk implements  Callable<Text<Annotation>> {

        private final String fileName;
        private final TextProcessor<String, Annotation> textProcessor;

        public WorkingChunk(String fileName, TextProcessor<String, Annotation> textProcessor) {
            this.fileName = fileName;
            this.textProcessor = textProcessor;
        }

        @Override
        public Text<Annotation> call() throws Exception {
            try {
                logger.info("preparing file {}", fileName);
                Text<String> text = Text.asFileLocation(fileName);
                return textProcessor.process(text);
            } catch (Throwable t) {
                logger.error("Error while processing", t);
                throw new RuntimeException(t);
            }
        }
    }
}
