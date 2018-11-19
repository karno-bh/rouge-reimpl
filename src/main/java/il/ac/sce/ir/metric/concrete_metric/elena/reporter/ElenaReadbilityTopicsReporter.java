package il.ac.sce.ir.metric.concrete_metric.elena.reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.reporter.utils.CommonFileReporter;
import il.ac.sce.ir.metric.core.score.ReadabilityMetricScore;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ElenaReadbilityTopicsReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;

    private ExecutorService executorService;

    private ElenaReadabilityMetricScoreCalculator scoreCalculator;

    private List<String> topics;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public ElenaReadabilityMetricScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(ElenaReadabilityMetricScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {

        final FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();
        final FileSystemPath fileSystemPath = new FileSystemPath();
        final CommonFileReporter commonFileReporter = new CommonFileReporter();
        final Configuration configuration = getConfiguration();
        String workingSetDirectory = configuration.getWorkingSetDirectory();

        File topicsDir = fileSystemTopologyResolver.getTopicsDir(workingSetDirectory, processedCategory);
        List<String> allTopicFiles = fileSystemTopologyResolver.getAllTopicRelativeFiles(workingSetDirectory, processedCategory);

        List<Future<ProcessedChunk<ReadabilityMetricScore>>> asyncChunks = new ArrayList<>();
        for (String topicFile : allTopicFiles) {
            String absoluteTopicFileName = fileSystemPath.combinePath(topicsDir.getAbsolutePath(), topicFile);
            Text<String> processedFileText = new Text<>(topicFile, absoluteTopicFileName);
            ProcessedChunk<Text<String>> processedChunk = new ProcessedChunk.Builder<Text<String>>()
                    .checkChunkData(true)
                    .checkTopicData(true)
                    .checkPeerFileName(false)
                    .processedCategory(processedCategory)
                    .metric(metric)
                    .topic(topicFile)
                    .chunkData(processedFileText)
                    .build();

            AsyncScoreCalculator<Text<String>, ReadabilityMetricScore> asyncScoreCalculator =
                    new AsyncScoreCalculator<>(processedChunk, getScoreCalculator());
            Future<ProcessedChunk<ReadabilityMetricScore>> asyncChunk = getExecutorService().submit(asyncScoreCalculator);
            asyncChunks.add(asyncChunk);
        }


    }

}
