package il.ac.sce.ir.metric.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableTabulator {

    private final List<List<String>> table;

    private String resultTable;

    private List<String> resultLines = new ArrayList<>();

    private List<int[]> lineRanges = new ArrayList<>();

    public TableTabulator(List<List<String>> table) {
        this.table = table;
    }

    public void tableAsTabularString() {
        int[] columnSizes = new int[table.get(0).size()];
        for (int i = 0; i < columnSizes.length; i++) {
            columnSizes[i] = Integer.MIN_VALUE;
        }
        for (List<String> line : table) {
            for (int i = 0; i < line.size(); i++) {
                int length = line.get(i).length();
                if (length > columnSizes[i]) {
                    columnSizes[i] = length;
                }
            }
        }
        int beginIndex = 0;
        for (int i = 0; i < columnSizes.length; i++) {
            int columnSize = columnSizes[i];
            int endIndex = beginIndex + columnSize + 1;
            lineRanges.add(new int[]{beginIndex, endIndex});
            beginIndex += columnSize + 2;
        }
        StringBuilder sb = new StringBuilder(256);
        for (List<String> line : table) {
            StringBuilder lineBuilder = new StringBuilder();
            for (int i = 0; i < line.size(); i++) {
                String column = line.get(i);
                int length = column.length();
                for (int j = 0; j <= columnSizes[i] - length; j++) {
                    sb.append(' ');
                    lineBuilder.append(' ');
                }
                sb.append(column);
                lineBuilder.append(column);
                if (i + 1 != line.size()) {
                    sb.append(' ');
                    lineBuilder.append(' ');
                }
            }
            sb.append('\n');
            resultLines.add(lineBuilder.toString());
        }
        this.resultTable = sb.toString();
    }

    public String getResultTable() {
        return resultTable;
    }

    public List<String> getResultLines() {
        return resultLines;
    }

    public List<int[]> getLineRanges() {
        return lineRanges;
    }
}
