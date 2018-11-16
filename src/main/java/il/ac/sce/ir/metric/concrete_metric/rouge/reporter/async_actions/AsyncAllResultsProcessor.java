package il.ac.sce.ir.metric.concrete_metric.rouge.reporter.async_actions;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.utils.converter.ObjectToMapConverter;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AsyncAllResultsProcessor implements Callable<Void> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<Future<ProcessedPeer<Score>>> allResultsPromisses;

    private final Configuration configuration;

    public AsyncAllResultsProcessor(List<Future<ProcessedPeer<Score>>> allResultsPromisses, Configuration configuration) {
        Objects.requireNonNull(allResultsPromisses, "Result Promisses cannot be null");
        Objects.requireNonNull(configuration, "Configuration could not be null");
        this.allResultsPromisses = allResultsPromisses;
        this.configuration = configuration;
    }

    public List<Future<ProcessedPeer<Score>>> getAllResultsPromisses() {
        return allResultsPromisses;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public Void call() throws Exception {
        List<Future<ProcessedPeer<Score>>> allResultsPromisses = getAllResultsPromisses();
        List<ProcessedPeer<Score>> reportedBundles = new ArrayList<>();
        for (Future<ProcessedPeer<Score>> reportedBundleFuture : allResultsPromisses) {
            try {
                ProcessedPeer<Score> reportedBundle = reportedBundleFuture.get();
                reportedBundles.add(reportedBundle);
            } catch (InterruptedException e) {
                logger.error("Main thread pool was interrupted", e);
                throw new IllegalStateException("Main thread pool was interrupted", e);
            } catch (ExecutionException e) {
                logger.error("Got exception while calculating ReportedBundle");
                throw new RuntimeException("Got exception while calculating ReportedBundle", e);
            }
        }

        groupAndReport(reportedBundles);
        return null;
    }

    protected void groupAndReport(List<ProcessedPeer<Score>> reportedBundles) {

        Map<String, List<ProcessedPeer<Score>>> groups = new HashMap<>();

        MessageFormat keyFormat = new MessageFormat("{0}_{1}_{2}");
        for (final ProcessedPeer<Score> reportedBundle : reportedBundles) {
            String key = keyFormat.format(new Object[]{
                    reportedBundle.getProcessedCategory().getDirLocation(),
                    reportedBundle.getProcessedSystem().getDirLocation(),
                    reportedBundle.getMetric()
            });
            List<ProcessedPeer<Score>> bundlesPerFile = groups.computeIfAbsent(key, _key -> new ArrayList<>());
            bundlesPerFile.add(reportedBundle);
        }
        dumpAllFiles(groups);
    }

    protected void dumpAllFiles(Map<String, List<ProcessedPeer<Score>>> groups) {
        FileSystemPath fileSystemPath = new FileSystemPath();
        Configuration configuration = getConfiguration();
        groups.forEach(
            (groupKey, reportedBundlesPerFile) -> {
                ProcessedPeer<Score> firstBundle = reportedBundlesPerFile.get(0);
                String resultFileName = constructResultFileName(firstBundle.getProcessedCategory(),
                        firstBundle.getProcessedSystem(),
                        firstBundle.getMetric()).toString();
                File resultFile = new File(fileSystemPath.combinePath(configuration.getResultDirectory(),
                        resultFileName) + Constants.CSV_EXTENSION);

                ObjectToMapConverter objectToMapConverter = new ObjectToMapConverter();
                Map<String, Object> properties = objectToMapConverter.getReportedProperties(firstBundle.getPeerData());
                Set<String> sortedKeys = new TreeSet<>(properties.keySet());
                String header = buildHeader(sortedKeys);

                try (PrintWriter pw = new PrintWriter(
                        new BufferedWriter(
                                new FileWriter(resultFile)
                        )
                )) {
                    pw.println(header);
                    for (ProcessedPeer<Score> reportedBundle : reportedBundlesPerFile) {
                        Map<String, Object> reportedProperties = objectToMapConverter.getReportedProperties(reportedBundle.getPeerData());
                        StringBuilder reportLineBuf = new StringBuilder(256);
                        reportLineBuf.append(reportedBundle.getPeerFileName());
                        convertMetricsToReportLine(reportedProperties, sortedKeys, reportLineBuf);
                        String reportLine = reportLineBuf.toString();
                        pw.println(reportLine);
                    }
                } catch (IOException ioe) {
                    throw new RuntimeException("Error while writing a report for " + resultFileName, ioe);
                }
            }
        );
    }

    private String buildHeader(Set<String> sortedKeys) {
        StringBuilder headerBuf = new StringBuilder(256);
        headerBuf.append(Constants.PEER);

        for (String key : sortedKeys) {
            headerBuf.append(Constants.CSV_REPORT_SEPARATOR).append(key);
        }
        return headerBuf.toString();
    }

    private StringBuilder constructResultFileName(ProcessedCategory processedCategory, ProcessedSystem processedSystem, String metric) {
        StringBuilder resultFileNameBuf = new StringBuilder(256);
        resultFileNameBuf.append(processedCategory.getDescription());
        String processedSystemDesc = processedSystem.getDescription().trim();

        int separatorLastIndex = processedSystemDesc.lastIndexOf(File.separator);
        if (separatorLastIndex != -1) {
            processedSystemDesc = processedSystemDesc.substring(separatorLastIndex + 1);
        }
        resultFileNameBuf
                .append(Constants.RESULT_FILE_ENITITIES_SEPARATOR).append(processedSystemDesc)
                .append(Constants.RESULT_FILE_ENITITIES_SEPARATOR).append(metric);
        return resultFileNameBuf;
    }

    private void convertMetricsToReportLine(Map<String, Object> properties,
                                            Collection<String> sortedKeys,
                                            StringBuilder reportLineBuf) {
        for (String key : sortedKeys) {
            Number scoreValue = (Number)properties.get(key);
            reportLineBuf.append(Constants.CSV_REPORT_SEPARATOR).append(scoreValue.doubleValue());
        }
    }


}
