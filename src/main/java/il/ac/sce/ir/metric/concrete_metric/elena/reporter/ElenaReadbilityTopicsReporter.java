package il.ac.sce.ir.metric.concrete_metric.elena.reporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.data.Text;
import il.ac.sce.ir.metric.core.reporter.Reporter;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.utils.CommonFileReporter;
import il.ac.sce.ir.metric.core.score.ReadabilityMetricScore;
import il.ac.sce.ir.metric.concrete_metric.elena.score.ElenaReadabilityMetricScoreCalculator;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemTopologyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ElenaReadbilityTopicsReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Configuration configuration;

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

    @Override
    public void report(ProcessedCategory processedCategory, String metric) {

        final FileSystemTopologyResolver fileSystemTopologyResolver = new FileSystemTopologyResolver();
        final CommonFileReporter commonFileReporter = new CommonFileReporter();
        final Configuration configuration = getConfiguration();
        String workingSetDirectory = configuration.getWorkingSetDirectory();

        Map<String, List<String>> topicFileLocationsPerTopic = fileSystemTopologyResolver.getTopicsWithFileNames(workingSetDirectory,
                processedCategory, topicFile -> topicFile.substring(0, 4));

        topicFileLocationsPerTopic.forEach((topic, files) -> {

        });

        for (Map.Entry<String, List<String>> topic : topicFileLocationsPerTopic.entrySet()) {
            String resultFileName = configuration.getResultDirectory() + File.separator +
                    processedCategory.getDescription() + Constants.RESULT_FILE_ENITITIES_SEPARATOR
                    + topic.getKey() + Constants.RESULT_FILE_ENITITIES_SEPARATOR + metric + Constants.CSV_EXTENSION;

            boolean headerCreated = false;
            for (String topicFile : topic.getValue()) {
                logger.info("Processing file: {}", topicFile);

                Text<String> text = Text.asFileLocation(topicFile);
                
                ReadabilityMetricScore readabilityMetricScore = getScoreCalculator().computeScore(text);

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> scoreAsMap = objectMapper.convertValue(readabilityMetricScore, Map.class);

                Map<String, Object> filteredProperties = new HashMap<>();
                for (Map.Entry<String, Object> prop : scoreAsMap.entrySet()) {
                    String propertyName = prop.getKey();
                    if (readabilityMetricScore.resolveReportedProperties().contains(propertyName)) {
                        filteredProperties.put(propertyName, prop.getValue());
                    }
                }
                scoreAsMap = filteredProperties;

                Set<String> sortedKeys = new TreeSet<>(scoreAsMap.keySet());

                try (PrintWriter pw = new PrintWriter(
                        new BufferedWriter(
                                new FileWriter(new File(resultFileName), true)
                        )
                )) {
                    if (!headerCreated) {
                        String header = commonFileReporter.buildHeader(sortedKeys);
                        pw.println(header);
                        headerCreated = true;
                    }
                    String reportedTopicFile = topicFile;
                    int separatorLastIndex = reportedTopicFile.lastIndexOf(File.separator);
                    if (separatorLastIndex != -1) {
                        reportedTopicFile = reportedTopicFile.substring(separatorLastIndex + 1);
                    }

                    StringBuilder reportedLineBuf = new StringBuilder(256);
                    reportedLineBuf.append(reportedTopicFile);
                    for (String key : sortedKeys) {
                        Number scoreVal = (Number)scoreAsMap.get(key);
                        reportedLineBuf.append(Constants.CSV_REPORT_SEPARATOR).append(scoreVal.doubleValue());
                    }
                    pw.println(reportedLineBuf.toString());
                } catch (IOException ioe) {
                    throw new RuntimeException("Error while writing a report for " + resultFileName, ioe);
                }
            }
        }

    }

}
