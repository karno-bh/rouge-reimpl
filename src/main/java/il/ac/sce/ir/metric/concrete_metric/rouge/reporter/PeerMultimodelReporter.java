package il.ac.sce.ir.metric.concrete_metric.rouge.reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.score_calculator.PeerMultimodelScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class PeerMultimodelReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;

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

    private boolean headerCreated = false;

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {
        Configuration configuration = getConfiguration();

        String categoryDir = configuration.getWorkingSetDirectory() + File.separator + processedCategory.getDirLocation();
        String peersDirectoryName = categoryDir + File.separator + Constants.PEERS_DIRECTORY;
        File peersDirectory = new File(peersDirectoryName);
        if (!peersDirectory.isDirectory()) {
            throw new RuntimeException(peersDirectoryName + " is not a directory");
        }

        String modelsDirectoryName = categoryDir + File.separator + Constants.MODELS_DIRECTORY;
        File modelsDirectory = new File(modelsDirectoryName);
        if (!modelsDirectory.isDirectory()) {
            throw new RuntimeException(modelsDirectory + " is not a directory");
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
            headerCreated = false;
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
                String[] modelsArr = modelsDirectory.list((file, fileName) -> fileName.startsWith(peerFileName));
                if (modelsArr == null || modelsArr.length == 0) {
                    logger.warn("No models for peer {}", peerFileName);
                    continue;
                }
                List<Text<String>> fileTexts = Arrays.stream(modelsArr)
                        .map(modelFileName -> modelsDirectoryName + File.separator + modelFileName)
                        .map(Text::asFileLocation).collect(Collectors.toList());
                if (fileTexts == null || fileTexts.isEmpty()) {
                    logger.warn("No models found for peer {} in category {}", peerFileName, processedCategory.getDescription());
                    continue;
                }
                Text<String> peerText = Text.asFileLocation(processedSystemDirLocation + File.separator + peerFileName);
                scoreCalculator.setModels(fileTexts);
                scoreCalculator.setPeer(peerText);

                Score score = scoreCalculator.computeScore();
                reportConcreteSystem(processedCategory, processedSystem, metric, configuration, peerFileName, score);
            }
        }
    }

    protected void reportConcreteSystem(ProcessedCategory processedCategory, ProcessedSystem processedSystem,
                                        String metric, Configuration configuration, String fileName, Score score) {

        requireAndCreateDirectory(configuration);

        StringBuilder resultFileNameBuf = constructResultFileName(processedCategory, processedSystem, metric);

        String resultFileName = resultFileNameBuf.toString();

        File resultFile = new File(configuration.getResultDirectory() + File.separator + resultFileName + Constants.CSV_EXTENSION);

        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(
                        new FileWriter(resultFile, true)
                )
        )){
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> properties = objectMapper.convertValue(score, Map.class);
            Map<String, Object> filteredProperties = new HashMap<>();
            for (Map.Entry<String, Object> prop : properties.entrySet()) {
                String propertyName = prop.getKey();
                if (score.resolveReportedProperties().contains(propertyName)) {
                    filteredProperties.put(propertyName, prop.getValue());
                }
            }
            properties = filteredProperties;

            Set<String> sortedKeys = new TreeSet<>(properties.keySet());
            if (!headerCreated) {
                String header = buildHeader(sortedKeys);
                pw.println(header);
                headerCreated = true;
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