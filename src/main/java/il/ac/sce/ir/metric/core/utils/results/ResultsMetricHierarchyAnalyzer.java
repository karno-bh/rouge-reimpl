package il.ac.sce.ir.metric.core.utils.results;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.data.ColoredCell;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.core.utils.math.RangeMapper;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class ResultsMetricHierarchyAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(ResultsMetricHierarchyAnalyzer.class);

    private static final Color[] NEGATIVE_PALETTE; // = HSBPalette.genPalette(256, Color.WHITE, Color.RED);
    private static final Color[] POSITIVE_PALETTE; // = HSBPalette.genPalette(256, Color.WHITE, Color.GREEN);
    static {
        NEGATIVE_PALETTE = new Color[256];
        for (int i = 0; i < 256; i++) {
            NEGATIVE_PALETTE[i] = new Color(255, 255 - i, 255 - i);
        }
        POSITIVE_PALETTE = new Color[256];
        for (int i = 0; i < 256; i++) {
            POSITIVE_PALETTE[i] = new Color(255 - i, 255, 255 - i);
        }
    }

    private static final Map<String, Boolean> METRIC_HEAT_DIRECTION = Collections.unmodifiableMap(new HashMap<String, Boolean>(){{
        put(Constants.FLESCH_READING_EASE, true);
        put(Constants.FLESCH_READING_EASE_NORMALIZED, true);
        put(Constants.AVERAGE_WORD_LENGTH, false);
        put(Constants.AVERAGE_WORD_LENGTH_NORMALIZED, false);
        put(Constants.AVERAGE_SENTENCE_LENGTH, false);
        put(Constants.AVERAGE_SENTENCE_LENGTH_NORMALIZED, false);
        put(Constants.NOUN_RATIO, false);
        put(Constants.PRONOUN_RATIO, false);
        put(Constants.PROPER_NOUN_RATIO, true);
        put(Constants.UNIQUE_PROPER_NOUN_RATIO, true);
        put(Constants.WORD_VARIATION_INDEX, true);
        put(Constants.WORD_VARIATION_INDEX_NORMALIZED, true);
    }});

    private final Map<String, Object> originalMetricHierarchy;

    private Map<String, Map<String, Map<String, Map<String, Double>>>> systemAverages;
    private Map<String, Map<String, Map<String, List<Map<String, String>>>>> flattenedMetricData;
    private Map<String, Map<String, Map<String, Map<String, Double>>>> virtualTopicSystemAverages;


    public ResultsMetricHierarchyAnalyzer(Map<String, Object> originalMetricHierarchy) {
        Objects.requireNonNull(originalMetricHierarchy, "Original Metric Hierarchy Cannot be Null");
        this.originalMetricHierarchy = originalMetricHierarchy;
    }

    public Map<String, Object> getOriginalMetricHierarchy() {
        return originalMetricHierarchy;
    }

    public Map<String, Map<String, Map<String, Map<String, Double>>>> getSystemAverages() {
        return systemAverages;
    }

    public Map<String, Map<String, Map<String, List<Map<String, String>>>>> getFlattenedMetricData() {
        return flattenedMetricData;
    }

    public void calculateDerivatives() {
        systemAverages = new HashMap<>();
        flattenedMetricData = new HashMap<>();
        Map<String, Map<String, Map<String, Double>>> valuesCount = new HashMap<>();
        originalMetricHierarchy.forEach((categoryKey, categoryData) -> {
            Map<String, Map> categoryDataMap = (Map<String, Map>) categoryData;
            categoryDataMap.forEach((systemKey, systemData) -> {
                Map<String, Object> systemDataMap = systemData;
                systemDataMap.forEach((metricKey, metricData) -> {
                    // System.out.println("System: " + systemKey + ", Metric Data: " + metricData);
                    List<String> subMetrics;
                    boolean dataOnDeeperLevel = false;
                    if (Constants.VIRTUAL_TOPIC_SYSTEM.equals(systemKey)) {
                        dataOnDeeperLevel = true;
                        if (metricKey.contains(Constants.READABILITY_LOWER_CASE)) {
                            subMetrics = Arrays.asList(Constants.ELENA_READABILITY_SUB_METRICS);
                        } else {
                            throw new IllegalStateException("Unknown metric");
                        }
                    } else {
                        if (metricKey.contains(Constants.ROUGE_LOWER_CASE)) {
                            subMetrics = Arrays.asList(Constants.ROUGE_SUB_METRICS);
                        } else if (metricKey.contains(Constants.AUTO_SUMM_ENG_LOWER_CASE)) {
                            dataOnDeeperLevel = true;
                            subMetrics = new ArrayList<>();
                            subMetrics.addAll(Arrays.asList(Constants.AUTO_SUMM_ENG_WORD_SUB_METRICS));
                            subMetrics.addAll(Arrays.asList(Constants.AUTO_SUMM_ENG_CHAR_SUB_METRICS));
                        } else if (metricKey.contains(Constants.READABILITY_LOWER_CASE)) {
                            subMetrics = Arrays.asList(Constants.ELENA_READABILITY_SUB_METRICS);
                        } else {
                            throw new IllegalStateException("Unknown metric");
                        }
                    }
                    List<Map<String, String>> subMetricsData;
                    if (!dataOnDeeperLevel) {
                        subMetricsData = (List<Map<String, String>>) metricData;
                    } else {
                        subMetricsData = new ArrayList<>();
                        Map<String, List<Map<String, String>>> nestedLevel = (Map<String, List<Map<String, String>>>) metricData;
                        nestedLevel.forEach((topicOrModelKey, subMetricsChunk) -> {
                            subMetricsData.addAll(subMetricsChunk);
                        });
                    }
                    // System.out.println("SubMetricsData: " + subMetricsData);


                    Map<String, Map<String, List<Map<String, String>>>> flattenedMetricCategoryData = flattenedMetricData.computeIfAbsent(categoryKey, k -> new HashMap<>());
                    Map<String, List<Map<String, String>>> flattenedMetricSystemData = flattenedMetricCategoryData.computeIfAbsent(systemKey, k -> new HashMap<>());
                    List<Map<String, String>> flattenedMetricMetricData = flattenedMetricSystemData.computeIfAbsent(metricKey, k -> new ArrayList<>());


                    Map<String, Map<String, Map<String, Double>>> averCategoryData = systemAverages.computeIfAbsent(categoryKey, k -> new HashMap<>());
                    Map<String, Map<String, Double>> averSystemData = averCategoryData.computeIfAbsent(systemKey, k -> new HashMap<>());
                    Map<String, Double> averMetricData = averSystemData.computeIfAbsent(metricKey, k -> new HashMap<>());

                    Map<String, Map<String, Double>> countCategoryData = valuesCount.computeIfAbsent(categoryKey, k -> new HashMap<>());
                    Map<String, Double> countSystemData = countCategoryData.computeIfAbsent(systemKey, k -> new HashMap<>());
                    // int i = 0;
                    for (Map<String, String> subMetricsDataElem : subMetricsData) {
                        flattenedMetricMetricData.add(subMetricsDataElem);
                        for (String subMetric : subMetrics) {
                            try {
                                if (subMetric != null) {
                                    String s = subMetricsDataElem.get(subMetric);
                                    if (s == null) {
                                        s = "100000000.0";
                                    }
                                    final double subMetricVal = Double.parseDouble(s);
                                    averMetricData.compute(subMetric, (k, v) -> v == null ? subMetricVal : v + subMetricVal);
                                }
                            } catch (Exception e) {
                                log.error("Cannot parse {} / {} / {}", systemKey, metricKey, subMetric, e);
                            }
                        }
                        // i++;
                        // MUST start from 1! at least one occurrence if here
                        countSystemData.compute(metricKey, (k, v) -> v == null ? 1d : ++v);
                    }
                });
            });
        });
        /*System.out.println("System Averages: " + systemAverages);
        System.out.println("Values Count" + valuesCount);*/
        systemAverages.forEach((categoryKey, categoryData) -> {
            categoryData.forEach((systemKey, systemData) -> {
                systemData.forEach((metricKey, metricData) -> {
                    double totalCountPerMetric = valuesCount
                            .get(categoryKey)
                            .get(systemKey)
                            .get(metricKey);
                    metricData.replaceAll((k, v) -> v / totalCountPerMetric);
                });
            });
        });
        /*System.out.println("System Averages: " + systemAverages);
        System.out.println("Values Count" + valuesCount);*/
        calculateVirtualSystemAverages();
    }

    private void calculateVirtualSystemAverages() {
        virtualTopicSystemAverages = new HashMap<>();
        Map<String, Map<String, Map<String, Double>>> counts = new HashMap<>();
        originalMetricHierarchy.forEach((categoryKey, system) -> {
            Map<String, Map> systemMap = (Map<String, Map>) system;
            Map<String, Map> metrics = systemMap.get(Constants.VIRTUAL_TOPIC_SYSTEM);
            if (metrics ==  null || metrics.isEmpty()) {
                return;
            }
            metrics.forEach((metricKey, topics) -> {
                List<String> subMetrics;
                if (Constants.ELENA_TOPICS_READABILITY_LOWER_CASE.equals(metricKey)) {
                    subMetrics = Arrays.asList(Constants.ELENA_READABILITY_SUB_METRICS);
                } else {
                    return;
                }
                Map<String, List<Map<String, String>>> topicsMap = (Map<String, List<Map<String,String>>>) topics;
                // System.out.println("Topics Map: " + topicsMap);
                topicsMap.forEach((topicKey, subMetricValues) -> {

                    Map<String, Map<String, Map<String, Double>>> virtualSystemMetrics = virtualTopicSystemAverages.computeIfAbsent(categoryKey, k -> new HashMap<>());
                    Map<String, Map<String, Double>> virtualSystemTopic = virtualSystemMetrics.computeIfAbsent(metricKey, k -> new HashMap<>());
                    Map<String, Double> subMetricAverages = virtualSystemTopic.computeIfAbsent(topicKey, k -> new HashMap<>());

                    Map<String, Map<String, Double>> countVirtSystemMetrics = counts.computeIfAbsent(categoryKey, k -> new HashMap<>());
                    Map<String, Double> countOfTopicSubmetrics = countVirtSystemMetrics.computeIfAbsent(metricKey, k -> new HashMap<>());

                    for (Map<String, String> subMetricDataElem : subMetricValues) {
                        for (String subMetric : subMetrics) {
                            final double subMetricVal = Double.parseDouble(subMetricDataElem.get(subMetric));
                            subMetricAverages.compute(subMetric, (k,v) -> v == null ? subMetricVal : v + subMetricVal);
                        }
                        countOfTopicSubmetrics.compute(topicKey, (k,v) -> v == null ? 1d : ++v);
                    }
                });
            });
        });

        virtualTopicSystemAverages.forEach((categoryKey, metric) -> {
            metric.forEach((metricKey, topic) -> {
                topic.forEach((topicKey, subMetrics) -> {
                    double totalTopicCount = counts.get(categoryKey).get(metricKey).get(topicKey);
                    subMetrics.replaceAll((k, v) -> v / totalTopicCount);
                });
            });
        });
        // System.out.println("End");
    }

    public Map<String, Set<String>> getAvailableSystems() {
        Map<String, Set<String>> availableSystem = new HashMap<>();
        originalMetricHierarchy.forEach((category, systemData) -> {
            Map<String, Map> systemDataMap = (Map<String, Map>) systemData;
            systemDataMap.forEach((system, metricData) -> {
                Set<String> systemsPerCategory = availableSystem.computeIfAbsent(category, k -> new HashSet<>());
                systemsPerCategory.add(system);
            });
        });
        return availableSystem;
    }

    public Set<String> getAvailableSystemMetrics(String category, String system) {
        Map<String, Map> systems = (Map<String, Map>)originalMetricHierarchy.get(category);
        Map<String, Map> metrics = systems.get(system);
        return metrics == null ? Collections.emptySet() : metrics.keySet();
    }

    public Set<String> getAvailableSubMetric(String category, String system, String metric) {
        Map<String, Map> systems = (Map<String, Map>)originalMetricHierarchy.get(category);
        Map<String, Map> metrics = systems.get(system);
        // Map<String, Map> subMetrics = metrics.get(metric);
        if (metric.contains(Constants.ROUGE_LOWER_CASE)) {
            return new LinkedHashSet<>(Arrays.asList(Constants.ROUGE_SUB_METRICS));
        }
        if (metric.contains(Constants.AUTO_SUMM_ENG_LOWER_CASE)) {
            Map<String, List> peers = metrics.get(metric);
            // assuming the table is consistent
            String firstAvailablePeer = peers.keySet().iterator().next();
            List<Map<String, String>> subMetrics = peers.get(firstAvailablePeer);
            Set<String> wordProperties = new LinkedHashSet<>(Arrays.asList(Constants.AUTO_SUMM_ENG_WORD_SUB_METRICS));
            Set<String> charProperties = new LinkedHashSet<>(Arrays.asList(Constants.AUTO_SUMM_ENG_CHAR_SUB_METRICS));
            Set<String> combinedProperties  = new LinkedHashSet<>();
            String someWordSubMetric = subMetrics.get(0).get(wordProperties.iterator().next());
            if (Double.parseDouble(someWordSubMetric) != -1d) {
                combinedProperties.addAll(wordProperties);
            }
            String someCharSubMetric = subMetrics.get(0).get(charProperties.iterator().next());
            if (Double.parseDouble(someCharSubMetric) != -1d) {
                combinedProperties.addAll(charProperties);
            }
            return combinedProperties;
            //System.out.println("sub metrics: " + subMetrics);
        }
        if (metric.contains(Constants.READABILITY_LOWER_CASE)) {
            return new LinkedHashSet<>(Arrays.asList(Constants.ELENA_READABILITY_SUB_METRICS));
        }

        throw new IllegalStateException("Cannot parse metric");
    }

    public Table asAverageTable(String category, Map<String, Map<String, Boolean>> metrics) {
        Map<String, Map<String, Map<String, Double>>> averSystemData = systemAverages.get(category);

        /*List<String> header = new ArrayList<>();
        header.add(Constants.SYSTEM);
        metrics.forEach((metricKey, subMetrics) -> {
            subMetrics.forEach((subMetricKey, enabled) -> {
                header.add(metricKey + " / " + subMetricKey);
            });
        });

        Table table = new Table();
        table.setColumns(header);*/
        Table table = new Table();
        List<String> header = new ArrayList<>();
        table.setColumns(header);
        boolean[] headerGenerated = {false};
        averSystemData.forEach((systemKey, metric) -> {
            if (Constants.VIRTUAL_TOPIC_SYSTEM.equals(systemKey)) {
                return;
            }
            if (!headerGenerated[0]) {
                header.add(Constants.SYSTEM);
            }
            List<Object> row = new ArrayList<>();
            row.add(systemKey);
            metric.forEach((metricKey, subMetric) -> {
                subMetric.forEach((subMetricKey, value) -> {
                    try {
                        Map<String, Boolean> chosenMetric = metrics.get(metricKey);
                        if (chosenMetric != null && chosenMetric.get(subMetricKey)) {
                            if (!headerGenerated[0]) {
                                header.add(metricKey + " / " + subMetricKey);
                            }
                            row.add(value);
                        }
                    } catch (Exception e) {
                        log.error("Cannot parse {} / {}", metricKey, subMetricKey, e);
                    }

                });
            });
            table.addRow(row);
            headerGenerated[0] = true;
        });
        return table;
    }

    public Table asTopicTable(String category, Map<String, Boolean> selectedSystems, Map<String, Map<String, Boolean>> selectedMetric) {
        Map<String, Map> systems = (Map<String, Map>) originalMetricHierarchy.get(category);
        Map<String, Map<String, Map<String, Double>>> virtualSystemMetrics = virtualTopicSystemAverages.get(category);


        Map<String, Map<String, Map<String, Map<String, Double>>>> topicsMap = new HashMap<>();
        /*selectedSystems.forEach((systemKey, enabled) -> {
            if (enabled == null || !enabled) {
                return;
            }*/

        virtualSystemMetrics.forEach((metricKey, topics) -> {
            topics.forEach((topicKey, subMetric) -> {
                subMetric.forEach((subMetricKey, value) -> {
                    Map<String, Boolean> selectedSubMetrics = selectedMetric.get(metricKey);
                    if (selectedSubMetrics != null) {
                        Boolean selectedSubMetric = selectedSubMetrics.get(subMetricKey);
                        if (selectedSubMetric != null && selectedSubMetric) {
                            // systemMap.computeIfAbsent(metricKey, )
                            Map<String, Map<String, Map<String, Double>>> systemMap = topicsMap.computeIfAbsent(topicKey, k -> new HashMap<>());
                            Map<String, Map<String, Double>> metricMap = systemMap.computeIfAbsent(Constants.VIRTUAL_TOPIC_SYSTEM, k -> new HashMap<>());
                            Map<String, Double> subMetricMap = metricMap.computeIfAbsent(metricKey, k -> new HashMap<>());
                            subMetricMap.put(subMetricKey, value);
                            selectedSystems.forEach((systemKey, enabled) -> {
                                if (enabled == null || !enabled) {
                                    return;
                                }
                                String peerMetricKey = Constants.TOPICS_METRICS_MAPPING.get(metricKey);
                                // System.out.println("System: " + systemKey + " Topic: " + topicKey + " Metric: " + metricKey + " Sub Metric: " + subMetricKey + " Val: " + value);
                                Map<String, List<Map<String, String>>> metrics = systems.get(systemKey);
                                List<Map<String, String>> peerValues = metrics.get(peerMetricKey);
                                Map<String, String> foundEl = null;
                                try {
                                    for (Map<String, String> peerValue : peerValues) {
                                        if (topicKey.equals(peerValue.get(Constants.PEER))) {
                                            foundEl = peerValue;
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (foundEl == null) {
                                    throw new IllegalStateException("Cannot find peer per topic: " + topicKey +
                                    " in category: " + category + ", system: " + systemKey + ", metric: " + peerMetricKey);
                                }
                                double peerVal = Double.parseDouble(foundEl.get(subMetricKey));
                                Map<String, Map<String, Double>> peerMetricMap = systemMap.computeIfAbsent(systemKey, k -> new HashMap<>());
                                Map<String, Double> peerSubMetrics = peerMetricMap.computeIfAbsent(peerMetricKey, k -> new HashMap<>());
                                peerSubMetrics.put(subMetricKey, peerVal);
                            });

                        }
                    }
                });
            });
        });
        //});

        Table table = new Table();
        List<String> header = new ArrayList<>();
        table.setColumns(header);
        boolean[] headersGenerated = {false};
        topicsMap.forEach((topicKey, _systems) -> {
            if (!headersGenerated[0]) {
                header.add(Constants.TOPIC);
            }
            List<Object> row = new ArrayList<>();
            row.add(topicKey);
            _systems.forEach((systemKey, metrics) -> {
                metrics.forEach((metricKey, subMetrics) -> {
                    subMetrics.forEach((subMetricKey, value) -> {
                        if (!headersGenerated[0]) {
                            if (Constants.VIRTUAL_TOPIC_SYSTEM.equals(systemKey)) {
                                header.add(metricKey + " / " + subMetricKey);
                            } else {
                                header.add(systemKey + " / " + metricKey + " / " + subMetricKey);
                            }
                        }
                        row.add(value);
                    });
                });
            });
            table.addRow(row);
            headersGenerated[0] = true;
        });

        return table;
    }

    public CategoryDataset tableToCategoryDataSet(Table table) {
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        List<List<Object>> tableData = table.getData();
        List<String> columns = table.getColumns();
        for(List<Object> row : tableData) {
            String system = null;
            for (int i = 0; i < row.size(); i++) {
                Object columnVal = row.get(i);
                if (i == 0) {
                    system = (String) columnVal;
                    continue;
                }
                String columnName = columns.get(i);
                double columnValue = (double) columnVal;
                defaultCategoryDataset.addValue(columnValue, columnName, system);
            }
        }

        return defaultCategoryDataset;
    }

    public Map<String, List<Double>> asFlattenedData(String category, Map<String, Map<String, Boolean>> metrics) {
        // MultiNotchedBoxData multiNotchedBoxData = new MultiNotchedBoxData(true);
        Map<String, Map<String, List<Map<String, String>>>> flattenSystemData = flattenedMetricData.get(category);
        Map<String, List<Double>> flattenedSystemMetricSubMetric = new TreeMap<>();
        flattenSystemData.forEach((systemKey, metric) -> {
            if (Constants.VIRTUAL_TOPIC_SYSTEM.equals(systemKey)) {
                return;
            }
            metric.forEach((metricKey, values) -> {
                for (Map<String, String> metricVal : values) {
                    metricVal.forEach((subMetricKey, subMetricValue) -> {
                        try {
                            Map<String, Boolean> chosenMetric = metrics.get(metricKey);
                            if (chosenMetric != null) {
                                Boolean subMetricSelection = chosenMetric.get(subMetricKey);
                                if (subMetricSelection != null && subMetricSelection) {
                                    String compoundKey = systemKey + " / " + metricKey + " / " + subMetricKey;
                                    List<Double> subMetricValues = flattenedSystemMetricSubMetric.computeIfAbsent(compoundKey, k -> new ArrayList<>());
                                    subMetricValues.add(Double.parseDouble(subMetricValue));
                                }
                            }
                        } catch (Exception e) {
                           log.error("Cannot parse {} / {}", metricKey, subMetricKey, e);
                        }

                    });
                }
            });
        });
        /*flattenedSystemMetricSubMetric.forEach((compoundKey, values) -> {
            double[] asDoubleArr = new double[values.size()];
            for(int i = 0; i < asDoubleArr.length; i++) {
                asDoubleArr[i] = values.get(i);
            }
            multiNotchedBoxData.add(compoundKey, asDoubleArr);
        });*/
        return flattenedSystemMetricSubMetric;
    }

    public Map<String, List<String>> getVirtualSTopicSystemMetrics(String category) {
        Map<String, Map> systems = (Map<String, Map>)originalMetricHierarchy.get(category);
        Map<String, Map> metrics = systems.get(Constants.VIRTUAL_TOPIC_SYSTEM);
        Map<String, List<String>> virtualTopicSystemMetrics = new HashMap<>();
        metrics.forEach((metricKey, topics) -> {
            /*if (topics.isEmpty()) {
                return;
            }*/
            if (Constants.ELENA_TOPICS_READABILITY_LOWER_CASE.equals(metricKey)) {
                virtualTopicSystemMetrics.put(metricKey, Arrays.asList(Constants.ELENA_READABILITY_SUB_METRICS));
            } else {
                throw new IllegalStateException("Unknown topic");
            }
        });
        return virtualTopicSystemMetrics;
    }

    public int getTopicColumn(Table table) {
        int index = 0;
        for (String header : table.getColumns()) {
            if (header.contains(Constants.TOPICS_READABILITY_LOWER_CASE)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void addAverageToTableAndSort(Table table) {
        List<List<Object>> data = table.getData();
        Map<Integer, Double> averages = new HashMap<>();
        double rowsNum = 0;
        for (List<Object> row : data) {
            int columnNum = 0;
            for (Object column : row) {
                if (column instanceof Double) {
                    double columnVal = (double) column;
                    averages.compute(columnNum, (k, v) -> v == null ? columnVal : v + columnVal);
                }
                columnNum++;
            }
            rowsNum++;
        }
        final double _rowsNum = rowsNum;
        averages.replaceAll((k, v) -> v / _rowsNum);

        // assuming table has as a first column a string and others are double

        data.sort((l, r) -> {
            String leftStr = (String) l.get(0);
            String rightString = (String) r.get(0);
            return leftStr.compareTo(rightString);
        });
        List<Object> row = new ArrayList<>();
        row.add(Constants.AVERAGE_VIRTUAL_ROW);
        for (int i = 1; i < data.get(0).size(); i++) {
            Double average = averages.get(i);
            row.add(average);
        }
        data.add(row);

        int topicColumn = getTopicColumn(table);
        List<String> columns = table.getColumns();
        List<String> sortedMetricColumns = new ArrayList<>();
        HashMap<String, Integer> originalOrder = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i);
            originalOrder.put(columnName, i);
            if (i != 0 && i != topicColumn) {
                sortedMetricColumns.add(columnName);
            }
        }

        sortedMetricColumns.sort(String::compareTo);
        List<String> newColumns = new ArrayList<>();
        newColumns.add(columns.get(0));
        newColumns.add(columns.get(topicColumn));
        for (String column : sortedMetricColumns) {
            int origIndex = originalOrder.get(column);
            newColumns.add(columns.get(origIndex));
        }

        Map<Integer, Integer> newToOldIndexMapping = new HashMap<>();
        for (int i = 0; i < newColumns.size(); i++) {
            int oldIndex = 0;
            if (i == 0) {
                oldIndex = 0;
            } else if (i == 1) {
                oldIndex = topicColumn;
            } else {
                oldIndex = originalOrder.get(newColumns.get(i));
            }
            newToOldIndexMapping.put(i, oldIndex);
        }

        List<List<Object>> newData = new ArrayList<>();
        for (List<Object> oldRow : data) {
            List<Object> newRow = new ArrayList<>();
            for (int i = 0; i < oldRow.size(); i++) {
                newRow.add(oldRow.get(newToOldIndexMapping.get(i)));
            }
            newData.add(newRow);
        }

        table.setColumns(newColumns);
        table.setData(newData);

    }

    public LinkedHashMap<String, List<ColoredCell>> tableToHeatMap(String metric, Table table, int basisColumn) {
        List<List<Object>> data = table.getData();
        LinkedHashMap<String, List<ColoredCell>> heatMap = new LinkedHashMap<>();
        for (List<Object> row : data) {
            int columnNum = 0;
            String rowName = null;
            double basisColumnVal = (double)row.get(basisColumn);
            double diffMin = Double.POSITIVE_INFINITY;
            double diffMax = Double.NEGATIVE_INFINITY;
            for (Object column : row) {
                if (columnNum == 0) {
                    rowName = (String) column;
                } else {
                    List<ColoredCell> coloredCells = heatMap.computeIfAbsent(rowName, k -> new ArrayList<>());
                    if (basisColumn != columnNum) {
                        double columnVal = (double) column;
                        ColoredCell coloredCell = new ColoredCell();
                        double diff = columnVal - basisColumnVal;
                        if (diff < diffMin) {
                            diffMin = diff;
                        }
                        if (diff > diffMax) {
                            diffMax = diff;
                        }
                        coloredCell.setValue(diff);
                        coloredCells.add(coloredCell);
                        coloredCell.setTableIndex(columnNum);
                    }
                }
                columnNum++;
            }

            List<ColoredCell> values = heatMap.get(rowName);

            final RangeMapper negativeMapper = new RangeMapper(0, Math.abs(diffMin), 0, 255);;
            final RangeMapper positiveMapper = new RangeMapper(0, diffMax, 0, 255);

            final Color[] goodPalette;
            final Color[] badPalette;
            if (METRIC_HEAT_DIRECTION.get(metric)) {
                /*negativeMapper = new RangeMapper(0, Math.abs(diffMin), 0, 255);
                positiveMapper = new RangeMapper(0, diffMax, 0, 255);*/
                goodPalette = POSITIVE_PALETTE;
                badPalette = NEGATIVE_PALETTE;
            } else {
                /*positiveMapper = new RangeMapper( 0, Math.abs(diffMin),0, 255);
                negativeMapper = new RangeMapper(0, diffMax, 0, 255);*/
                goodPalette = NEGATIVE_PALETTE;
                badPalette = POSITIVE_PALETTE;
            }
            values.forEach(v -> {
                double diff = v.getValue();
                Color[] palette;
                RangeMapper mapper;
                if (diff < 0) {
                    palette = badPalette;
                    mapper = negativeMapper;
                } else {
                    palette = goodPalette;
                    mapper = positiveMapper;
                }
                int paletteIndex = (int)mapper.map(Math.abs(diff));
                v.setColor(palette[paletteIndex]);
            });
        }
        return heatMap;
    }

    public List<List<String>> tableToStringRepresentation(Table table, Function<String, String> headerFormatter,
                                                   Function<String, String> stringValFormatter) {
        List<List<String>> strTable = new ArrayList<>();
        List<String> columns = table.getColumns();
        List<String> header = new ArrayList<>();
        for (String column : columns) {
            String newColumn = headerFormatter.apply(column);
            header.add(newColumn);
        }
        strTable.add(header);
        List<List<Object>> data = table.getData();
        DecimalFormat df = new DecimalFormat("#.####");
        for (List<Object> row : data) {
            List<String> strRow = new ArrayList<>();
            for (Object columnVal : row) {
                String newColumnVal;
                if (columnVal instanceof Double) {
                    double d = (double) columnVal;
                    newColumnVal = df.format(d);
                } else {
                    newColumnVal = (String) columnVal;
                    newColumnVal = stringValFormatter.apply(newColumnVal);
                }
                strRow.add(newColumnVal);
            }
            strTable.add(strRow);
        }

        return strTable;
    }

    public LinkedHashMap<String, List<ColoredCell>> asRawColoredCells(Table table) {
        LinkedHashMap<String, List<ColoredCell>> rawData = new LinkedHashMap<>();
        List<List<Object>> tableData = table.getData();

        for (List<Object> tableDatum : tableData) {
            int columnNum = 0;
            List<ColoredCell> data = new ArrayList<>();
            for (Object cell : tableDatum) {
                if (columnNum == 0) {
                    rawData.put((String) cell, data);
                } else {
                    double val = (double) cell;
                    ColoredCell coloredCell = new ColoredCell();
                    coloredCell.setValue(val);

                    data.add(coloredCell);
                }
                columnNum++;
            }
        }

        return rawData;
    }
}
