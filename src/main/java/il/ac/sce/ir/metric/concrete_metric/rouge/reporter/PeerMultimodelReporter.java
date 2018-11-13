package il.ac.sce.ir.metric.concrete_metric.rouge.reporter;

import il.ac.sce.ir.metric.concrete_metric.rouge.reporter.data.ReportedBundle;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import il.ac.sce.ir.metric.core.score_calculator.data.MultiModelPair;
import il.ac.sce.ir.metric.core.utils.converter.ObjectToMapConverter;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemCommons;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PeerMultimodelReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;

    private ExecutorService executorService;

    private PeerMultimodelScoreCalculator scoreCalculator;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public PeerMultimodelScoreCalculator getScoreCalculator() {
        return scoreCalculator;
    }

    public void setScoreCalculator(PeerMultimodelScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    //    private boolean headerCreated = false;

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {
        Configuration configuration = getConfiguration();
        FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();
        FileSystemPath fileSystemPath = new FileSystemPath();
        FileSystemCommons fileSystemCommons = new FileSystemCommons();

        fileSystemCommons.requireAndCreateResultDirectory(configuration.getResultDirectory());
        String workingSetDirectory = configuration.getWorkingSetDirectory();

        File modelsDirectory = fileSystemTopologyResolver.getModelsDirectory(workingSetDirectory, processedCategory);

        List<ProcessedSystem> processedSystems = fileSystemTopologyResolver.getProcessedSystems(workingSetDirectory, processedCategory);
        if (processedSystems == null || processedSystems.isEmpty()) {
            logger.warn("Category {} does not have any system", processedCategory.getDirLocation());
            return;
        }

        List<Future<ReportedBundle>> scoresFutures = new ArrayList<>();
        for (ProcessedSystem processedSystem : processedSystems) {
            boolean headerCreated[] = {false}; // hacky way to propagate...
            String processedSystemDirLocation = processedSystem.getDirLocation();
            List<String> peerFileNames = fileSystemTopologyResolver.getPeerFileNames(processedSystem);
            if (peerFileNames.isEmpty()) {
                logger.warn("System {} does not have any file", processedSystem.getDescription());
                continue;
            }

            for (final String peerFileName : peerFileNames) {
                List<Text<String>> modelsPerPeer = fileSystemTopologyResolver.getModelTextsPerPeer(modelsDirectory, peerFileName);
                if (modelsPerPeer == null || modelsPerPeer.isEmpty()) {
                    logger.warn("No models found for peer {} in category {}", peerFileName, processedCategory.getDescription());
                    continue;
                }
                Text<String> peerText = Text.asFileLocation(fileSystemPath.combinePath(processedSystemDirLocation, peerFileName));
                MultiModelPair multiModelPair = new MultiModelPair(peerText, modelsPerPeer);

                Score score = getScoreCalculator().computeScore(multiModelPair);
                ReportedBundle reportedBundle = new ReportedBundle.Builder()
                        .processedCategory(processedCategory)
                        .processedSystem(processedSystem)
                        .metric(metric)
                        .peerFileName(peerFileName)
                        .score(score)
                        .build();

                reportConcreteSystem(processedCategory, processedSystem, metric, peerFileName, score, headerCreated);
            }
        }
    }

    protected void reportConcreteSystem(ProcessedCategory processedCategory, ProcessedSystem processedSystem,
                                        String metric, String fileName, Score score, boolean[] headerCreated) {

        Configuration configuration = getConfiguration();

        StringBuilder resultFileNameBuf = constructResultFileName(processedCategory, processedSystem, metric);

        String resultFileName = resultFileNameBuf.toString();

        File resultFile = new File(configuration.getResultDirectory() + File.separator + resultFileName + Constants.CSV_EXTENSION);

        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(
                        new FileWriter(resultFile, true)
                )
        )){
            ObjectToMapConverter objectToMapConverter = new ObjectToMapConverter();
            Map<String, Object> properties = objectToMapConverter.getReportedProperties(score);

            Set<String> sortedKeys = new TreeSet<>(properties.keySet());
            if (!headerCreated[0]) {
                String header = buildHeader(sortedKeys);
                pw.println(header);
                headerCreated[0] = true;
            }
            String reportedPeerFileName = fileName;
            int separatorLastIndex = reportedPeerFileName.lastIndexOf(File.separator);
            if (separatorLastIndex != -1) {
                reportedPeerFileName = reportedPeerFileName.substring(separatorLastIndex + 1);
            }

            StringBuilder reportLineBuf = new StringBuilder(256);
            reportLineBuf.append(fileName);
            for (String key : sortedKeys) {
                Number scoreValue = (Number)properties.get(key);
                reportLineBuf.append(Constants.CSV_REPORT_SEPARATOR).append(scoreValue.doubleValue());
            }
            String reportLine = reportLineBuf.toString();
            pw.println(reportLine);
        } catch(IOException ioe) {
            throw new RuntimeException("Error while writing a report for " + resultFileName, ioe);
        }
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

}
