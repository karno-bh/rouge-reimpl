package il.ac.sce.ir.metric.core.reporter.utils;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedCategory;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedPeer;
import il.ac.sce.ir.metric.core.reporter.file_system_reflection.ProcessedSystem;
import il.ac.sce.ir.metric.core.score.ReportedProperties;
import il.ac.sce.ir.metric.core.score.Score;
import il.ac.sce.ir.metric.core.utils.converter.ObjectToMapConverter;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;

import java.io.*;
import java.util.*;

public class CommonPeerFileReporter {


    public <T extends ReportedProperties> void reportConcreteFile(List<ProcessedPeer<T>> reportedBundlesPerFile, String resultDirectory) {
        final FileSystemPath fileSystemPath = new FileSystemPath();
        final CommonFileReporter commonFileReporter = new CommonFileReporter();
        ProcessedPeer<T> firstBundle = reportedBundlesPerFile.get(0);
        String resultFileName = constructResultFileName(firstBundle.getProcessedCategory(),
                firstBundle.getProcessedSystem(),
                firstBundle.getMetric());
        File resultFile = new File(fileSystemPath.combinePath(resultDirectory,
                resultFileName) + Constants.CSV_EXTENSION);

        ObjectToMapConverter objectToMapConverter = new ObjectToMapConverter();
        Map<String, Object> properties = objectToMapConverter.getReportedProperties(firstBundle.getPeerData());
        Set<String> sortedKeys = new TreeSet<>(properties.keySet());
        String header = commonFileReporter.buildHeader(sortedKeys);

        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(
                        new FileWriter(resultFile)
                )
        )) {
            pw.println(header);
            for (ProcessedPeer<T> reportedBundle : reportedBundlesPerFile) {
                Map<String, Object> reportedProperties = objectToMapConverter.getReportedProperties(reportedBundle.getPeerData());
                StringBuilder reportLineBuf = new StringBuilder(256);
                reportLineBuf.append(reportedBundle.getPeerFileName());
                commonFileReporter.convertMetricsToReportLine(reportedProperties, sortedKeys, reportLineBuf);
                String reportLine = reportLineBuf.toString();
                pw.println(reportLine);
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Error while writing a report for " + resultFileName, ioe);
        }
    }

    private String constructResultFileName(ProcessedCategory processedCategory, ProcessedSystem processedSystem, String metric) {
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
        return resultFileNameBuf.toString();
    }

}
