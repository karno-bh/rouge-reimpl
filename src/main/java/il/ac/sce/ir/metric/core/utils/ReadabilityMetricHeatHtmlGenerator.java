package il.ac.sce.ir.metric.core.utils;

import il.ac.sce.ir.metric.core.gui.data.ColoredCell;

import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;

public class ReadabilityMetricHeatHtmlGenerator {

    private String resultDirectory;

    private String fileName;

    private String diffHeatTableName;

    private String originalValuesName;

    private HtmlSuitableTableRepresentation metricHeatTable;

    private HtmlSuitableTableRepresentation originalValuesTable;

    private DecimalFormat valuesFormat;

    private Function<String, String> headerFormatter;

    public String getResultDirectory() {
        return resultDirectory;
    }

    public void setResultDirectory(String resultDirectory) {
        this.resultDirectory = resultDirectory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDiffHeatTableName() {
        return diffHeatTableName;
    }

    public void setDiffHeatTableName(String diffHeatTableName) {
        this.diffHeatTableName = diffHeatTableName;
    }

    public String getOriginalValuesName() {
        return originalValuesName;
    }

    public void setOriginalValuesName(String originalValuesName) {
        this.originalValuesName = originalValuesName;
    }

    public HtmlSuitableTableRepresentation getMetricHeatTable() {
        return metricHeatTable;
    }

    public void setMetricHeatTable(HtmlSuitableTableRepresentation metricHeatTable) {
        this.metricHeatTable = metricHeatTable;
    }

    public HtmlSuitableTableRepresentation getOriginalValuesTable() {
        return originalValuesTable;
    }

    public void setOriginalValuesTable(HtmlSuitableTableRepresentation originalValuesTable) {
        this.originalValuesTable = originalValuesTable;
    }

    public DecimalFormat getValuesFormat() {
        return valuesFormat;
    }

    public void setValuesFormat(DecimalFormat valuesFormat) {
        this.valuesFormat = valuesFormat;
    }

    public Function<String, String> getHeaderFormatter() {
        return headerFormatter;
    }

    public void setHeaderFormatter(Function<String, String> headerFormatter) {
        this.headerFormatter = headerFormatter;
    }

    public void saveAsHtml() throws IOException {
        StringBuilder mainStringBuilder = new StringBuilder(64 * 1024);
        String htmlStart = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                "    <style>\n" +
                "        td {\n" +
                "            text-align: right;\n" +
                "        }\n" +
                "        div {\n" +
                "            margin: 10px;\n" +
                "        }\n" +
                "    </style>\n" +
                "    <title>Document</title>\n" +
                "</head>\n" +
                "<body>";
        mainStringBuilder.append(htmlStart);

        mainStringBuilder.append("<div>").append(diffHeatTableName).append("</div>");
        HtmlTableConverter heatTableConverter = new HtmlTableConverter(metricHeatTable.getHeader(), metricHeatTable.getData());
        heatTableConverter.convert(mainStringBuilder, valuesFormat, headerFormatter);

        mainStringBuilder.append("<div>").append(originalValuesName).append("</div>");
        HtmlTableConverter originalTableConverter = new HtmlTableConverter(originalValuesTable.getHeader(), originalValuesTable.getData());
        originalTableConverter.convert(mainStringBuilder, valuesFormat, headerFormatter);

        String htmlEnd = "\n" +
                "</body>\n" +
                "</html>";
        mainStringBuilder.append(htmlEnd);

        if (!fileName.endsWith(".html")) {
            fileName += ".html";
        }
        File resultFile = Paths.get(resultDirectory, fileName).toFile();
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "UTF-8")))) {
            pw.print(mainStringBuilder.toString());
        }
    }

    public static class HtmlSuitableTableRepresentation {

        private List<String> header;

        private LinkedHashMap<String, List<ColoredCell>> data;

        public List<String> getHeader() {
            return header;
        }

        public void setHeader(List<String> header) {
            this.header = header;
        }

        public LinkedHashMap<String, List<ColoredCell>> getData() {
            return data;
        }

        public void setData(LinkedHashMap<String, List<ColoredCell>> data) {
            this.data = data;
        }
    }
}
