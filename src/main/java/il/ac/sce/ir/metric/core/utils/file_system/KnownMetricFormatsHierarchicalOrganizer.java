package il.ac.sce.ir.metric.core.utils.file_system;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.StringUtils;

import java.util.*;

public class KnownMetricFormatsHierarchicalOrganizer {

    public Map<String, Object> organize(String... result) {
        AllCSVFilesLoader allCSVFilesLoader = new AllCSVFilesLoader(true);
        Map<String, List<Map<String, String>>> allCSVInDirectories = allCSVFilesLoader.getAllCSVInDirectories(result);
        Map<String, Object> hierarchy = new HashMap<>();
        allCSVFilesLoader.extractHierarchyStructure(
                allCSVInDirectories,
                Arrays.asList(Constants.METRICS_WITH_CATEGORY_SYSTEM_METRIC_FORMAT),
                hierarchy
        );
        hierarchy = processTopicMetrics(hierarchy);
        List<Map<String, String>> autoSummENGCSV = allCSVInDirectories.get(Constants.AUTO_SUMM_ENG_LOWER_CASE);
        if (autoSummENGCSV != null) {
            hierarchy = organizeAutoSummENG(autoSummENGCSV, hierarchy);
        }

        return hierarchy;
    }

    public Map<String, Object> processTopicMetrics(Map<String, Object> hierarchy) {
        Map<String, Object> newHierarchy = new HashMap<>();
        hierarchy.forEach((category, systemOrTopicMetric) -> {
            Map<String, Map> systemOrTopicMetricMap = (Map<String, Map>) systemOrTopicMetric;
            systemOrTopicMetricMap.forEach((systemOrTopic, metricValues) -> {
                Map<String, List> metricDataMap = (Map<String, List>) metricValues;
                boolean thisIsTopic = false;
                for (String key : metricDataMap.keySet()) {
                    if (key.contains(Constants.TOPIC)) {
                        thisIsTopic = true;
                    }
                }
                Map<String, Map> newSystemToMetrics = (Map<String, Map>)newHierarchy.computeIfAbsent(category, k -> new HashMap<>());
                if (thisIsTopic) {
                    Map<String, Map> virtualSystem = newSystemToMetrics.computeIfAbsent(Constants.VIRTUAL_TOPIC_SYSTEM, k -> new HashMap<>());
                    metricDataMap.forEach((metricKey, metricValuesList) -> {
                        Map<String, List> topicMap = virtualSystem.computeIfAbsent(metricKey, k -> new HashMap());
                        topicMap.put(systemOrTopic, metricValuesList);
                    });
                } else {
                    newSystemToMetrics.put(systemOrTopic, metricValues);
                }
            });
        });

        return newHierarchy;
    }

    public Map<String, Object> organizeAutoSummENG(List<Map<String, String>> autoSummENGCSV, Map<String, Object> groupByCategory) {
        StringUtils stringUtils = new StringUtils();
        // Map<String, Object> groupByCategory = new HashMap<>();
        autoSummENGCSV.forEach(csvLine -> {
            Map<String, Map> category =  (Map<String, Map>)groupByCategory.computeIfAbsent(csvLine.get(Constants.CATEGORY), k -> new HashMap());
            String system = csvLine.get(Constants.SYSTEM);
            system = stringUtils.getLastFileInString(system);
            Map<String, Map> systemData = category.computeIfAbsent(system, k -> new HashMap());
            Map<String, List> metricData = systemData.computeIfAbsent(Constants.AUTO_SUMM_ENG_LOWER_CASE, k -> new HashMap());
            String peer = csvLine.get(Constants.PEER);
            List peerData = metricData.computeIfAbsent(peer, k -> new ArrayList());
            peerData.add(csvLine);
        });
        return groupByCategory;
    }

}
