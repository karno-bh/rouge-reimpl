package il.ac.sce.ir.metric.core.utils;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.data.ColoredCell;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class HtmlTableConverter {

    private final List<String> header;

    private final LinkedHashMap<String, List<ColoredCell>> rows;

    public HtmlTableConverter(List<String> header, LinkedHashMap<String, List<ColoredCell>> rows) {
        Objects.requireNonNull(header, "Header cannot be null");
        Objects.requireNonNull(rows, "Rows cannot be null");
        this.header = header;
        this.rows = rows;
    }

    public StringBuilder convert(StringBuilder sb, DecimalFormat df, Function<String, String> headerFormatter) {

        sb.append("<table border=0>");

        sb.append("<thead>");
        sb.append("<tr>");
        for (String td : header) {
            if (headerFormatter != null) {
                td = headerFormatter.apply(td);
            }
            sb.append("<td>").append(td).append("</td>");
        }
        sb.append("</tr>");
        sb.append("</thead>");

        sb.append("<tbody>");
        for (Map.Entry<String, List<ColoredCell>> row : rows.entrySet()) {
            sb.append("<tr>");
            String key = row.getKey();
            key = Constants.AVERAGE_VIRTUAL_ROW.equals(key) ? "Avg" : key;
            sb.append("<td>").append(key).append("</td>");
            List<ColoredCell> values = row.getValue();
            for (ColoredCell value : values) {
                Color c = value.getColor();
                if (c != null) {
                    sb
                            .append("<td style='")
                            .append("background-color: rgb(")
                            .append(c.getRed()).append(',')
                            .append(c.getGreen()).append(',')
                            .append(c.getBlue()).append(")'>");
                } else {
                    sb.append("<td>");
                }
                sb.append(df.format(value.getValue())).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");
        return sb;
    }
}
