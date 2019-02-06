package il.ac.sce.ir.metric.core.utils.file_system;

import il.ac.sce.ir.metric.core.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllCSVFilesLoader {

    private final boolean exceptionSafe;

    public AllCSVFilesLoader(boolean exceptionSafe) {
        this.exceptionSafe = exceptionSafe;
    }

    public AllCSVFilesLoader() {
        this(false);
    }

    private static final Logger log = LoggerFactory.getLogger(AllCSVFilesLoader.class);

    public Map<String, List<Map<String, String>>> getAllCSVInDirectory(String resultDirectoryLocation) {
        File resultDirectory = new File(resultDirectoryLocation);
        if (!resultDirectory.isDirectory()) {
            if (exceptionSafe) return null;
            throw new RuntimeException("Result Directory should be a directory");
        }
        String[] resultFiles = resultDirectory.list((File dir, String name) -> name.toLowerCase().endsWith(Constants.CSV_EXTENSION));
        if (resultFiles == null) {
            log.warn("No results file in " + resultDirectoryLocation);
            return null;
        }
        Map<String, List<Map<String, String>>> fileContents = new HashMap<>();
        for (String resultFile : resultFiles) {
            String fullFileName = resultDirectoryLocation + File.separator + resultFile;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullFileName), StandardCharsets.UTF_8.name()))) {
                String fileLine;
                boolean headerProcessed = false;
                List<Map<String, String>> fileContent = new ArrayList<>();
                String[] orderedParams = null;
                while ( (fileLine = reader.readLine()) != null) {
                    if (!headerProcessed) {
                        orderedParams = fileLine.split(",");
                        headerProcessed = true;
                    } else {
                        String[] values = fileLine.split(",");
                        Map<String, String> processedLine = new HashMap<>();
                        for (int i = 0; i < values.length; i++) {
                            processedLine.put(orderedParams[i], values[i]);
                        }
                        fileContent.add(processedLine);
                    }
                }
                fileContents.put(resultFile.substring(0, resultFile.length() - Constants.CSV_EXTENSION.length()), fileContent);
            } catch (IOException ioe) {
                throw new RuntimeException("Cannot read file " + fullFileName, ioe);
            }
        }
        return fileContents;
    }

    public Map<String, List<Map<String, String>>> getAllCSVInDirectories(String... directories) {

        Map<String, List<Map<String, String>>> allDirectories = new HashMap<>();
        for (String directory : directories) {
            Map<String, List<Map<String, String>>> allCSVInDirectory = getAllCSVInDirectory(directory);
            if (allCSVInDirectory  != null) {
                allDirectories.putAll(allCSVInDirectory);
            }
        }
        return allDirectories;

    }

    public Map<String, Object> extractHierarchyStructure(Map<String, List<Map<String, String>>> fileContents,
                                                         List<String> metricsForSystem,
                                                         Map<String, Object> results) {
        Map<String, Object> intermediateResult = new HashMap<>();
        // TODO refactor being not so hacky...
        final int categoryIdx = 0, systemIdx = 1, topicIdx = 1;
        fileContents.forEach((String fileName, List<Map<String, String>> fileContent) -> {
            String foundMetricForSystem = null;
            for (String metricForSystem : metricsForSystem) {
                int index = fileName.indexOf(metricForSystem);
                /*if (metricForSystem.equals(GUIConstants.ELENA_TOPICS_READABILITY_LOWER_CASE)) {
                    System.out.println(Arrays.asList("Index", "" + index));
                }*/
                if (index != -1) {
                    foundMetricForSystem = fileName.substring(index);
                    break;
                }
            }
            if (foundMetricForSystem != null) {
                String[] fileMetadata = fileName.split("_");
                String category = fileMetadata[categoryIdx];
                String system = fileMetadata[systemIdx];

                Map<String, Map> categoryData = (Map<String, Map>)intermediateResult.get(category);
                if (categoryData == null) {
                    categoryData = new HashMap<>();
                    intermediateResult.put(category, categoryData);
                }
                Map<String, List> systemData = (Map<String, List>) categoryData.get(system);
                if (systemData == null) {
                    systemData = new HashMap<>();
                    categoryData.put(system, systemData);
                }
                systemData.put(foundMetricForSystem, fileContent);
            }

        });
        results.putAll(intermediateResult);
        return results;
    }
}
