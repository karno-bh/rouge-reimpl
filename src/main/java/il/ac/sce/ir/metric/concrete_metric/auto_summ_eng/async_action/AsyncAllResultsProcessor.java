package il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.async_action;

import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.score.SimpleTextNGramResultPair;
import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.util.ProcessedResultMapper;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.reporter.utils.CommonChunkFileReporter;
import il.ac.sce.ir.metric.core.reporter.utils.CommonFileReporter;
import il.ac.sce.ir.metric.core.sync.Arbiter;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AsyncAllResultsProcessor implements Callable<Void> {

    private static final Logger log = LoggerFactory.getLogger(AsyncAllResultsProcessor.class);

    private final List<Future<ProcessedChunk<SimpleTextNGramResultPair>>> promises;

    private final Arbiter arbiter;

    private final String metric;

    private final String resultDirectory;

    public AsyncAllResultsProcessor(List<Future<ProcessedChunk<SimpleTextNGramResultPair>>> promises, Arbiter arbiter, String metric, String resultDirectory) {
        this.promises = promises;
        this.arbiter = arbiter;
        this.metric = metric;
        this.resultDirectory = resultDirectory;
    }

    public List<Future<ProcessedChunk<SimpleTextNGramResultPair>>> getPromises() {
        return promises;
    }

    public Arbiter getArbiter() {
        return arbiter;
    }

    public String getMetric() {
        return metric;
    }

    public String getResultDirectory() {
        return resultDirectory;
    }

    @Override
    public Void call() throws Exception {
        try {
            List<ProcessedChunk<SimpleTextNGramResultPair>> results = new ArrayList<>();
            for (Future<ProcessedChunk<SimpleTextNGramResultPair>> promise : promises) {
                results.add(promise.get());
                // System.out.println("Processed!");
            }
            printResult(results);
            return null;
        } catch (Exception e) {
            log.error("Error while processing all results for Auto Summ ENG", e);
            return null;
        } finally {
            if (getArbiter() != null) {
                getArbiter().signalizeFinish(getMetric());
            }
        }
    }

    private void printResult(List<ProcessedChunk<SimpleTextNGramResultPair>> results) throws IOException {
        FileSystemPath fileSystemPath = new FileSystemPath();
        ProcessedResultMapper processedResultMapper = new ProcessedResultMapper();
        CommonChunkFileReporter fileReporter = new CommonChunkFileReporter();
        CommonFileReporter commonFileReporter = new CommonFileReporter();

        String resultFileName = fileSystemPath.combinePath(resultDirectory, metric) + Constants.CSV_EXTENSION;
        File resultFile = new File(resultFileName);


        try (PrintWriter pw = fileReporter.asWriter(resultFile)) {
            String header = commonFileReporter.buildHeader(processedResultMapper.getHeader());
            pw.println(header);
            StringBuilder lineBuf = new StringBuilder(1024);
            for (ProcessedChunk<SimpleTextNGramResultPair> result : results) {
                lineBuf.setLength(0);
                lineBuf.append(result.getProcessedCategory().getDescription())
                        .append(Constants.CSV_REPORT_SEPARATOR)
                        .append(result.getProcessedSystem().getDescription())
                        .append(Constants.CSV_REPORT_SEPARATOR)
                        .append(result.getPeerFileName())
                        .append(Constants.CSV_REPORT_SEPARATOR)
                        .append(result.getModel());

                Map<String, Double> flatResult = processedResultMapper.asMap(result.getChunkData());
                commonFileReporter.convertMetricsToReportLine((Map)flatResult, processedResultMapper.getScoreHeader(), lineBuf);
                pw.println(lineBuf.toString());
            }
        }
    }

    public static class Builder {

        private List<Future<ProcessedChunk<SimpleTextNGramResultPair>>> promises;

        private Arbiter arbiter;

        private String metric;

        private String resultDirectory;

        public Builder promises(List<Future<ProcessedChunk<SimpleTextNGramResultPair>>> promises) {
            this.promises = promises;
            return this;
        }

        public Builder arbiter(Arbiter arbiter) {
            this.arbiter = arbiter;
            return this;
        }

        public Builder metric(String metric) {
            this.metric = metric;
            return this;
        }

        public Builder resultDirectory(String resultDirectory) {
            this.resultDirectory = resultDirectory;
            return this;
        }

        public AsyncAllResultsProcessor build() {
            Objects.requireNonNull(promises, "Promises should not be null");
            Objects.requireNonNull(metric, "Metric should not be null");
            Objects.requireNonNull(resultDirectory, "Result Directory should not be null");

            return new AsyncAllResultsProcessor(promises, arbiter, metric, resultDirectory);
        }
    }
}
