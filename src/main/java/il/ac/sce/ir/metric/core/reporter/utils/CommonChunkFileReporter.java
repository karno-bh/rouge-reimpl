package il.ac.sce.ir.metric.core.reporter.utils;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunk;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedChunkType;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score.ReportedProperties;
import il.ac.sce.ir.metric.core.utils.converter.ObjectToMapConverter;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;

import java.io.*;
import java.util.*;

public class CommonChunkFileReporter {


    public <T extends ReportedProperties> void reportConcreteFile(List<ProcessedChunk<T>> reportedBundlesPerFile, String resultDirectory) {
        final FileSystemPath fileSystemPath = new FileSystemPath();
        final CommonFileReporter commonFileReporter = new CommonFileReporter();
        ProcessedChunk<T> firstBundle = reportedBundlesPerFile.get(0);
        String resultFileName = constructResultFileName(firstBundle);
        File resultFile = new File(fileSystemPath.combinePath(resultDirectory,
                resultFileName) + Constants.CSV_EXTENSION);

        ObjectToMapConverter objectToMapConverter = new ObjectToMapConverter();
        Map<String, Object> properties = objectToMapConverter.getReportedProperties(firstBundle.getChunkData());
        Set<String> sortedKeys = new TreeSet<>(properties.keySet());
        String leftColumn = getLeftColumn(firstBundle);
        String header = commonFileReporter.buildHeader(sortedKeys, leftColumn);

        try (PrintWriter pw = asWriter(resultFile)) {
            pw.println(header);
            for (ProcessedChunk<T> reportedBundle : reportedBundlesPerFile) {
                Map<String, Object> reportedProperties = objectToMapConverter.getReportedProperties(reportedBundle.getChunkData());
                StringBuilder reportLineBuf = new StringBuilder(256);
                String leftColumnData = getLeftColumnData(reportedBundle);
                reportLineBuf.append(leftColumnData);
                commonFileReporter.convertMetricsToReportLine(reportedProperties, sortedKeys, reportLineBuf);
                String reportLine = reportLineBuf.toString();
                pw.println(reportLine);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error while writing a report for " + resultFileName, ioe);
        }
    }

    public PrintWriter asWriter(File resultFile) throws IOException {
        return new PrintWriter(
                new BufferedWriter(
                        new FileWriter(resultFile)
                )
        );
    }

    private String constructResultFileName(ProcessedChunk<?> processedChunk) {
        ProcessedCategory processedCategory = processedChunk.getProcessedCategory();
        ProcessedSystem processedSystem = processedChunk.getProcessedSystem();
        String topic = processedChunk.getTopic();
        String metric = processedChunk.getMetric();

        StringBuilder resultFileNameBuf = new StringBuilder(256);
        resultFileNameBuf.append(processedCategory.getDescription());

        String middle;
        switch (processedChunk.getChunkType()) {
            case TOPIC:
                middle = topic.substring(0, 4);
                break;
            case PEER_MULTI_MODEL:
                middle = processedSystem.getDescription().trim();
                break;
            default:
                throw new IllegalStateException("Unknown chunk type");
        }

        int separatorLastIndex = middle.lastIndexOf(File.separator);
        if (separatorLastIndex != -1) {
            middle = middle.substring(separatorLastIndex + 1);
        }
        resultFileNameBuf
                .append(Constants.RESULT_FILE_ENITITIES_SEPARATOR).append(middle)
                .append(Constants.RESULT_FILE_ENITITIES_SEPARATOR).append(metric);
        return resultFileNameBuf.toString();
    }

    private String getLeftColumn(ProcessedChunk<?> processedChunk) {
        switch (processedChunk.getChunkType()) {
            case TOPIC:
                return Constants.TOPIC;
            case PEER_MULTI_MODEL:
                return Constants.PEER;
            default:
                throw new IllegalStateException("Unknown chunk type");
        }
    }

    private String getLeftColumnData(ProcessedChunk<?> processedChunk) {
        switch (processedChunk.getChunkType()) {
            case TOPIC:
                return processedChunk.getTopic();
            case PEER_MULTI_MODEL:
                return processedChunk.getMetric();
            default:
                throw new IllegalStateException("Unknown chunk type");
        }
    }

}
