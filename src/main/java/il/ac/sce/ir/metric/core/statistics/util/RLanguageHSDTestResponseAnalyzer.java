package il.ac.sce.ir.metric.core.statistics.util;

import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.data.HSDTestLetterRepresentation;
import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.data.HSDTestLetterRepresentationRow;
import il.ac.sce.ir.metric.core.utils.switch_obj.SwitchObj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RLanguageHSDTestResponseAnalyzer {

    public static final String GROUPS_PARAM = "groups.groups";
    public static final String VALUE_PARAM = "groups.value";
    public static final String METRIC_PARAM = "_row";

    private final List<Map<String, Object>> originalResponse;

    public RLanguageHSDTestResponseAnalyzer(List<Map<String, Object>> originalResponse) {
        if (originalResponse == null || originalResponse.isEmpty()) {
            throw new IllegalArgumentException("Response should not be empty");
        }
        this.originalResponse = originalResponse;
    }

    public List<HSDTestLetterRepresentationRow> asHSDTestLetterRepresentation() {
        List<HSDTestLetterRepresentationRow> rows = new ArrayList<>();
        char maxGroup = 0;
        for (Map<String, Object> responseRow : originalResponse) {
            String metric = (String) responseRow.get(METRIC_PARAM);
            String groups = (String) responseRow.get(GROUPS_PARAM);
            double value = (double) responseRow.get(VALUE_PARAM);
            HSDTestLetterRepresentationRow row = new HSDTestLetterRepresentationRow();
            row.setGroups(groups);
            row.setMetric(metric);
            row.setMean(value);
            rows.add(row);

            char maxMetricGroup = groups.charAt(groups.length() - 1);
            if (maxMetricGroup > maxGroup) {
                maxGroup = maxMetricGroup;
            }
        }
        int maxGroupIndex = maxGroup - ('a' - 1);
        char[] groupsArr = new char[maxGroupIndex];
        for (HSDTestLetterRepresentationRow row : rows) {
            for (int i = 0; i < groupsArr.length; i++) groupsArr[i] = ' ';
            String groups = row.getGroups();
            for (int i = 0; i < groups.length(); i++) {
                char group = groups.charAt(i);
                groupsArr[group - 'a'] = group;
            }
            groups = new String(groupsArr);
            row.setGroups(groups);
        }
        // System.out.println(rows);
        return rows;
    }
}
