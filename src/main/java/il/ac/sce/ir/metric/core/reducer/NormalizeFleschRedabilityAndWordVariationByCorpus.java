package il.ac.sce.ir.metric.core.reducer;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.math.RangeMapper;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.*;

public class NormalizeFleschRedabilityAndWordVariationByCorpus implements Reducer {


    private static final String[] PROCESSED_METRICS = {
            Constants.ELENA_READABILITY_LOWER_CASE,
            Constants.ELENA_TOPICS_READABILITY_LOWER_CASE,
    };

    private static final Set<String> PROCESSED_METRICS_SET = new HashSet<>(Arrays.asList(PROCESSED_METRICS));

    private ReducerStore<Map<String, Object>> store;

    public void setStore(ReducerStore<Map<String, Object>> store) {
        this.store = store;
    }

    @Override
    public void reduce() {

        Map<String, Object> storeData = store.getStore();
        final double[] minMaxFlesch = {Double.MAX_VALUE, Double.MIN_VALUE};
        final double[] minMaxWordVariation = {Double.MAX_VALUE, Double.MIN_VALUE};
        final int min = 0;
        final int max = 1;
        storeData.forEach((category, systemOrTopicRawData) -> {
            Map<String, Object> systemOrTopicData = (Map<String, Object>) systemOrTopicRawData;
            systemOrTopicData.forEach((systemOrTopic, metricRawData) -> {
                Map<String, List<Map<String, String>>> metricsData = (Map<String, List<Map<String, String>>>) metricRawData;
                PROCESSED_METRICS_SET.forEach(metric -> {
                    // System.out.println(Arrays.asList(category, systemOrTopic, metric));
                    List<Map<String, String>> metricData = metricsData.get(metric);
                    if (metricData != null) {
                        metricData.forEach(metricRow -> {
                            String fleschReadingEaseRaw = metricRow.get(Constants.FLESCH_READING_EASE);
                            double fleschReadingEase = Double.parseDouble(fleschReadingEaseRaw);
                            if (fleschReadingEase < minMaxFlesch[min]) {
                                minMaxFlesch[min] = fleschReadingEase;
                            }
                            if (fleschReadingEase > minMaxFlesch[max]) {
                                minMaxFlesch[max] = fleschReadingEase;
                            }

                            String wordVariationIndexRaw = metricRow.get(Constants.WORD_VARIATION_INDEX);
                            double wordVariationIndex = Double.parseDouble(wordVariationIndexRaw);
                            if (wordVariationIndex < minMaxWordVariation[min]) {
                                minMaxWordVariation[min] = wordVariationIndex;
                            }

                            if (wordVariationIndex > minMaxWordVariation[max]) {
                                minMaxWordVariation[max] = wordVariationIndex;
                            }
                        });
                    }
                    // System.out.println(metricData);
                });
            });
        });

        Map<String, Object>  storeDataClone = SerializationUtils.clone((Serializable & Map<String, Object>) storeData);

        RangeMapper fleschNormalizerByWholeCorpus = new RangeMapper(minMaxFlesch[min], minMaxFlesch[max], 0, 1);
        RangeMapper wordVariationIndexNormalizerByWholeCorpus = new RangeMapper(minMaxWordVariation[min], minMaxWordVariation[max], 0, 1);

        storeDataClone.forEach((category, systemOrTopicRawData) -> {
            Map<String, Object> systemOrTopicData = (Map<String, Object>) systemOrTopicRawData;
            systemOrTopicData.forEach((systemOrTopic, metricRawData) -> {
                Map<String, List<Map<String, String>>> metricsData = (Map<String, List<Map<String, String>>>) metricRawData;
                PROCESSED_METRICS_SET.forEach(metric -> {
                    // System.out.println(Arrays.asList(category, systemOrTopic, metric));
                    List<Map<String, String>> metricData = metricsData.get(metric);
                    if (metricData != null) {
                        metricData.forEach(metricRow -> {
                            String fleschReadingEaseRaw = metricRow.get(Constants.FLESCH_READING_EASE);
                            double fleschReadingEase = Double.parseDouble(fleschReadingEaseRaw);
                            double normalizedFleschReadingEase = fleschNormalizerByWholeCorpus.map(fleschReadingEase);
                            metricRow.put(Constants.FLESCH_READING_EASE_NORMALIZED, "" + normalizedFleschReadingEase);

                            String wordVariationIndexRaw = metricRow.get(Constants.WORD_VARIATION_INDEX);
                            double wordVariationIndex = Double.parseDouble(wordVariationIndexRaw);
                            double normalizedwordVariationIndex = wordVariationIndexNormalizerByWholeCorpus.map(wordVariationIndex);
                            metricRow.put(Constants.WORD_VARIATION_INDEX_NORMALIZED, "" + normalizedwordVariationIndex);
                        });
                        // System.out.println(metricData);
                    }
                });
            });
        });

        store.updateStore( innerStore -> storeDataClone );

        // System.out.println(Arrays.asList("MinMax", "" + minMaxFlesch[min], "" + minMaxFlesch[max]));
    }
}
