package il.ac.sce.ir.metric.concrete_metric.elena.reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score.ReadabilityMetricScore;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;
import il.ac.sce.ir.metric.core.utils.converter.ObjectToMapConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ElenaReadabilityPeersReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;

    private ElenaReadabilityMetricScoreCalculator scoreCalculator;

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

    // private boolean headerCreated = false;

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {
        Configuration configuration = getConfiguration();

        String categoryDir = configuration.getWorkingSetDirectory() + File.separator + processedCategory.getDirLocation();
        String peersDirectoryName = categoryDir + File.separator + Constants.PEERS_DIRECTORY;
        File peersDirectory = new File(peersDirectoryName);
        if (!peersDirectory.isDirectory()) {
            throw new RuntimeException(peersDirectoryName + " is not a directory");
        }

        String[] systems = peersDirectory.list((file, name) -> file.isDirectory());
        if (systems == null || systems.length == 0) {
            logger.warn("Category {} does not have any system");
            return;
        }

        List<ProcessedSystem> processedSystems = Arrays.stream(systems)
                .map(systemDirName -> {
                    String pathName = peersDirectory + File.separator + systemDirName + File.separator + Constants.DESCRIPTION_FILE;
                    File systemDescriptionFile = new File(pathName);
                    String description = null;
                    if (systemDescriptionFile.isFile()) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(systemDescriptionFile), StandardCharsets.UTF_8.name()))) {
                            description = reader.readLine();
                        } catch (IOException ignored) {
                        }
                    }
                    return ProcessedSystem.as()
                            .dirLocation(peersDirectory + File.separator + systemDirName)
                            .description(description)
                            .build();
                })
                .collect(Collectors.toList());

        for (ProcessedSystem processedSystem : processedSystems) {
            boolean headerCreated[] = {false};
            String processedSystemDirLocation = processedSystem.getDirLocation();
            File processedSystemDir = new File(processedSystemDirLocation);
            String[] peerFileNames = processedSystemDir.list((file, fileName) -> {
                File dirFile = new File(processedSystemDirLocation + File.separator + fileName);
                return dirFile.isFile();
            });
            if (peerFileNames == null  || peerFileNames.length == 0) {
                logger.warn("System {} does not have any file", processedSystem.getDescription());
                continue;
            }
            for (final String peerFileName : peerFileNames) {
                Text<String> peerText = Text.asFileLocation(processedSystemDirLocation + File.separator + peerFileName);

                ReadabilityMetricScore score = scoreCalculator.computeScore(peerText);
                reportConcreteSystem(processedCategory, processedSystem, metric, configuration, peerFileName, score, headerCreated);
            }
        }
    }

    protected void reportConcreteSystem(ProcessedCategory processedCategory, ProcessedSystem processedSystem,
                                        String metric, Configuration configuration, String fileName, ReadabilityMetricScore score, boolean[] headerCreated) {
        requireAndCreateDirectory(configuration);

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

    private void requireAndCreateDirectory(Configuration configuration) {
        String resultDirectoryName = configuration.getResultDirectory();
        File resultDirectory = new File(resultDirectoryName);
        if (!resultDirectory.exists()) {
            boolean mkdirsOk = resultDirectory.mkdirs();
            if (!mkdirsOk) {
                throw new RuntimeException("Cannot create result directory: " + resultDirectoryName);
            }
        } else if (!resultDirectory.isDirectory()) {
            throw new RuntimeException(MessageFormat.format("Result Directory \"(0)\" is not a directory on file system", resultDirectory));
        }
    }
}
