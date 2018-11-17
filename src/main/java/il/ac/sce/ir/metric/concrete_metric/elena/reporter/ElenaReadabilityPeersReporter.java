package il.ac.sce.ir.metric.concrete_metric.elena.reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.async_action.AsyncAllResultsProcessor;
import il.ac.sce.ir.metric.core.async_action.AsyncScoreCalculator;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.reporter.template.AbstractPeerReporter;
import il.ac.sce.ir.metric.core.score.ReadabilityMetricScore;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;
import il.ac.sce.ir.metric.core.utils.converter.ObjectToMapConverter;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ElenaReadabilityPeersReporter extends AbstractPeerReporter<Future<ProcessedPeer<ReadabilityMetricScore>>> {

    private ElenaReadabilityMetricScoreCalculator scoreCalculator;

    public ElenaReadabilityMetricScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(ElenaReadabilityMetricScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    @Override
    protected void processConcretePeer(ProcessedPeer<Void> processedPeer, List<Future<ProcessedPeer<ReadabilityMetricScore>>> scoresCollector) {
        Text<String> peerText = Text.asFileLocation(getFileSystemPath().combinePath(processedPeer.getProcessedSystem().getDirLocation(),
                processedPeer.getPeerFileName()));

        ProcessedPeer<Text<String>> peerToProcess = new ProcessedPeer.Builder<Text<String>>()
                .processedCategory(processedPeer.getProcessedCategory())
                .processedSystem(processedPeer.getProcessedSystem())
                .metric(processedPeer.getMetric())
                .peerFileName(processedPeer.getPeerFileName())
                .peerData(peerText)
                .build();

        AsyncScoreCalculator<Text<String>, ReadabilityMetricScore> task = new AsyncScoreCalculator<>(peerToProcess, getScoreCalculator());
        Future<ProcessedPeer<ReadabilityMetricScore>> taskFuture = getExecutorService().submit(task);
        scoresCollector.add(taskFuture);
    }

    @Override
    protected void processResults(List<Future<ProcessedPeer<ReadabilityMetricScore>>> scoresCollector) {
        AsyncAllResultsProcessor<ReadabilityMetricScore> asyncAllResultsProcessor = new AsyncAllResultsProcessor<>(scoresCollector, getConfiguration());
        // getExecutorService().submit(asyncAllResultsProcessor);
        try {
            asyncAllResultsProcessor.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
