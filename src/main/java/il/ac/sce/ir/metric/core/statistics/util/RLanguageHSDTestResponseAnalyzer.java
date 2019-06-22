package il.ac.sce.ir.metric.core.statistics.util;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.data.HSDTestLetterRepresentationRow;
import il.ac.sce.ir.metric.core.utils.TableTabulator;

import java.util.*;

public class RLanguageHSDTestResponseAnalyzer {

    public static final String GROUPS = "groups.";
    public static final String GROUPS_GROUPS = "groups.groups";
    public static final String GROUPS_VALUE = "groups.value";

    public static final String STATISTICS = "Statistics";
    public static final String STATS = "stats.";
    public static final String STATS_MS_ERROR = "stats.MSerror";
    public static final String STATS_DF = "stats.Df";
    public static final String STATS_MEAN = "stats.Mean";
    public static final String STATS_CV = "stats.CV";

    public static final String TUKEY_TEST = "Tukey test";
    public static final String PARAMS = "params.";
    public static final String PARAMS_NTR = "params.ntr";
    public static final String PARAMS_STUDENTIZED_RANGE = "params.StudentizedRange";
    public static final String PARAMS_ALPHA = "params.alpha";

    public static final String GROUP_AND_MEANS = "Group & Means";
    public static final String MEANS = "means.";
    public static final String MEANS_VALUE = "means.value";
    public static final String MEANS_STD = "means.std";
    public static final String MEANS_R = "means.r";
    public static final String MEANS_MIN = "means.Min";
    public static final String MEANS_MAX = "means.Max";
    public static final String MEANS_Q25 = "means.Q25";
    public static final String MEANS_Q50 = "means.Q50";
    public static final String MEANS_Q75 = "means.Q75";


    public static final String METRIC = "Metric";
    public static final String METRIC_PARAM = "_row";

    private final List<Map<String, Object>> originalResponse;

    public RLanguageHSDTestResponseAnalyzer(List<Map<String, Object>> originalResponse) {
        if (originalResponse == null || originalResponse.isEmpty()) {
            throw new IllegalArgumentException("Response should not be empty");
        }
        this.originalResponse = originalResponse;
    }

    private double getDoubleVal(Object possibleDouble) {
        if (possibleDouble instanceof Integer) {
            return (int) possibleDouble;
        }
        return (double) possibleDouble;
    }

    public List<HSDTestLetterRepresentationRow> asHSDTestLetterRepresentation() {
        List<HSDTestLetterRepresentationRow> rows = new ArrayList<>();
        char maxGroup = 0;
        for (Map<String, Object> responseRow : originalResponse) {
            String metric = (String) responseRow.get(METRIC_PARAM);
            String groups = (String) responseRow.get(GROUPS_GROUPS);
            double value = getDoubleVal(responseRow.get(GROUPS_VALUE));

            double meansValue = getDoubleVal(responseRow.get(MEANS_VALUE));
            double meansStd = getDoubleVal(responseRow.get(MEANS_STD));
            int meansR = (int) responseRow.get(MEANS_R);
            double meansMin = getDoubleVal(responseRow.get(MEANS_MIN));
            double meansMax = getDoubleVal(responseRow.get(MEANS_MAX));
            double meansQ25 = getDoubleVal(responseRow.get(MEANS_Q25));
            double meansQ50 = getDoubleVal(responseRow.get(MEANS_Q50));
            double meansQ75 = getDoubleVal(responseRow.get(MEANS_Q75));

            HSDTestLetterRepresentationRow row = new HSDTestLetterRepresentationRow();
            row.setGroups(groups);
            row.setMetric(metric);
            row.setValue(value);

            row.setMeansValue(meansValue);
            row.setMeansStd(meansStd);
            row.setMeansR(meansR);
            row.setMeansMin(meansMin);
            row.setMeansMax(meansMax);
            row.setQ25(meansQ25);
            row.setQ50(meansQ50);
            row.setQ75(meansQ75);

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

    public Map<String, String> groupToTables() {
        String statistics = parseStatistics();
        String params = parseParams();
        String distros = parseDistributions();

        Map<String, String> tables = new HashMap<>();
        tables.put(STATISTICS, statistics);
        tables.put(TUKEY_TEST, params);
        tables.put(GROUP_AND_MEANS, distros);

        return tables;
    }

    private String parseStatistics() {
        Map<String, Object> anyRow = originalResponse.get(0);
        String msError = anyRow.get(STATS_MS_ERROR).toString();
        String df = anyRow.get(STATS_DF).toString();
        String mean = anyRow.get(STATS_MEAN).toString();
        String cv = anyRow.get(STATS_CV).toString();
        List<List<String>> table = new ArrayList<>();
        int statsLength = STATS.length();
        List<String> header = Arrays.asList(
                STATS_MS_ERROR.substring(statsLength),
                STATS_DF.substring(statsLength),
                STATS_MEAN.substring(statsLength),
                STATS_CV.substring(statsLength)
        );
        table.add(header);
        List<String> row = Arrays.asList(msError, df, mean, cv);
        table.add(row);

        TableTabulator tabulator = new TableTabulator(table);
        tabulator.tableAsTabularString();
        return tabulator.getResultTable();
    }

    private String parseDistributions() {
        List<List<String>> table = new ArrayList<>();
        int groupsLength = GROUPS.length();
        int meansLength = MEANS.length();
        List<String> header = Arrays.asList(
                METRIC,

                GROUPS_GROUPS.substring(groupsLength),
                GROUPS_VALUE.substring(groupsLength),

                MEANS_VALUE,
                MEANS_STD.substring(meansLength),
                MEANS_R.substring(meansLength),
                MEANS_MIN.substring(meansLength),
                MEANS_MAX.substring(meansLength),
                MEANS_Q25.substring(meansLength),
                MEANS_Q50.substring(meansLength),
                MEANS_Q75.substring(meansLength)
        );
        table.add(header);

        List<HSDTestLetterRepresentationRow> hsdTestLetterRepresentationRows = asHSDTestLetterRepresentation();
        for (HSDTestLetterRepresentationRow _row : hsdTestLetterRepresentationRows) {

            List<String> row = Arrays.asList(
                    _row.getMetric()
                            .replace(Constants.ELENA_READABILITY_LOWER_CASE, Constants.READABILITY_LOWER_CASE),

                    _row.getGroups(),
                    "" + _row.getValue(),

                    "" + _row.getMeansValue(),
                    "" + _row.getMeansStd(),
                    "" + _row.getMeansR(),
                    "" + _row.getMeansMin(),
                    "" + _row.getMeansMax(),
                    "" + _row.getQ25(),
                    "" + _row.getQ50(),
                    "" + _row.getQ75()
            );
            table.add(row);
        }

        TableTabulator tabulator = new TableTabulator(table);
        tabulator.tableAsTabularString();
        return tabulator.getResultTable();
    }

    private String parseParams() {
        Map<String, Object> anyRow = originalResponse.get(0);
        String ntr = anyRow.get(PARAMS_NTR).toString();
        String studentizedRange = anyRow.get(PARAMS_STUDENTIZED_RANGE).toString();
        String alpha = anyRow.get(PARAMS_ALPHA).toString();

        List<List<String>> table = new ArrayList<>();
        int paramsLength = PARAMS.length();
        List<String> header = Arrays.asList(
                PARAMS_NTR.substring(paramsLength),
                PARAMS_STUDENTIZED_RANGE.substring(paramsLength),
                PARAMS_ALPHA.substring(paramsLength)
        );
        table.add(header);

        List<String> row = Arrays.asList(ntr, studentizedRange, alpha);
        table.add(row);

        TableTabulator tabulator = new TableTabulator(table);
        tabulator.tableAsTabularString();
        return tabulator.getResultTable();
    }




}
