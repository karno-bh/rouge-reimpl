package il.ac.sce.ir.metric.core.reducer;

import il.ac.sce.ir.metric.core.container.data.Configuration;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.file_system.FileSystemPath;

import java.io.*;
import java.util.*;

public class FileSystemCSVSaver implements Reducer {

    private Configuration configuration;

    private ReducerStore<Map<String, Object>> reducerStore;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setReducerStore(ReducerStore<Map<String, Object>> reducerStore) {
        this.reducerStore = reducerStore;
    }

    @Override
    public void reduce() {
        FileSystemPath fsPath = new FileSystemPath();
        String reducersResultDirectoryPath = fsPath.combinePath(
                configuration.getResultDirectory(),
                Constants.REDUCERS_DUMP_DIRECTORY
        );
        File reducersResultDirectory = new File(reducersResultDirectoryPath);
        if (!reducersResultDirectory.exists()) {
            boolean created = reducersResultDirectory.mkdir();
            if (!created) {
                throw new RuntimeException("Cannot create reducers result directory: " + reducersResultDirectoryPath);
            }
        } else if (!reducersResultDirectory.isDirectory()) {
            throw new RuntimeException("Reducers result file: " + reducersResultDirectoryPath + " exists but file is not directory");
        }

        Map<String, Object> storeData = reducerStore.getStore();

        storeData.forEach((category, systemOrTopicRawData) -> {
            Map<String, Object> systemOrTopicData = (Map<String, Object>) systemOrTopicRawData;
            systemOrTopicData.forEach((systemOrTopic, metricRawData) -> {
                Map<String, List<Map<String, String>>> metricsData = (Map<String, List<Map<String, String>>>) metricRawData;
                metricsData.forEach((metric, metricData) -> {
                    String fileName = String.join(Constants.RESULT_FILE_ENITITIES_SEPARATOR, category, systemOrTopic, metric);
                    String fullFileName = fsPath.combinePath(
                        reducersResultDirectoryPath,
                        fileName
                    );
                    fullFileName += Constants.CSV_EXTENSION;

                    File resultFile = new File(fullFileName);
                    try (PrintWriter pw = new PrintWriter(
                            new BufferedWriter(
                                    new FileWriter(resultFile, true)
                            )
                    )) {

                        boolean first = true;
                        Set<String> properties = null;
                        for (Map<String, String> metricRow : metricData) {
                            if (first) {
                                properties = new TreeSet<>(metricRow.keySet());
                                String shouldBeFisrtColumn = null;
                                if (properties.contains(Constants.TOPIC)) {
                                    shouldBeFisrtColumn = Constants.TOPIC;
                                } else if (properties.contains(Constants.PEER)) {
                                    shouldBeFisrtColumn = Constants.PEER;
                                }
                                if (shouldBeFisrtColumn != null) {
                                    Set<String> oldProps = properties;
                                    properties = new LinkedHashSet<>();
                                    properties.add(shouldBeFisrtColumn);
                                    oldProps.remove(shouldBeFisrtColumn);
                                    properties.addAll(oldProps);
                                }
                                String[] sortedKeysArrays = properties.toArray(new String[0]);
                                String headerLine = String.join(Constants.CSV_REPORT_SEPARATOR, sortedKeysArrays);
                                pw.println(headerLine);
                                first = false;
                            }
                            List<String> values = new ArrayList<>(properties.size());
                            for (String key : properties) {
                                values.add(metricRow.get(key));
                            }
                            String dataLine = String.join(Constants.CSV_REPORT_SEPARATOR, values.toArray(new String[0]));
                            pw.println(dataLine);
                        }
                    } catch (IOException ioe) {
                        throw new RuntimeException("Cannot write file", ioe);
                    }
                });
            });
        });
    }


}
