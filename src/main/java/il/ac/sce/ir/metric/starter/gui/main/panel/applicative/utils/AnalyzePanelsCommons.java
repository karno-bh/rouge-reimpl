package il.ac.sce.ir.metric.starter.gui.main.panel.applicative.utils;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.core.utils.converter.GraphicsToSVGExporter;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.SVGSavePanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyzePanelsCommons {

    public boolean hasSelection(Map<String, Map<String, Boolean>> selectedMetrics, Component component) {
        for (Map.Entry<String, Map<String, Boolean>> metricSelection : selectedMetrics.entrySet()) {
            Map<String, Boolean> selectedSubMetrics = metricSelection.getValue();
            if (selectedSubMetrics == null) {
                continue;
            }
            for (Map.Entry<String, Boolean> selectedSubMetric : selectedSubMetrics.entrySet()) {
                Boolean selected = selectedSubMetric.getValue();
                if (selected != null && selected) {
                    return true;
                }
            }
        }
        JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(component), "Please select at least one metric");
        return false;
    }

    public void filterTableDisplaying(Table table) {
        List<String> newColumns = new ArrayList<>();
        for (String column : table.getColumns()) {
            String newColumn = column.replace(Constants.ELENA_READABILITY_LOWER_CASE, Constants.READABILITY_LOWER_CASE);
            newColumn = newColumn.replace(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE, Constants.TOPICS_READABILITY_LOWER_CASE);
            newColumns.add(newColumn);
        }
        table.setColumns(newColumns);
    }

    public void exportChartAsSVG(JFreeChart chart, SVGSavePanel svgSavePanel, String resultDirectory) {
        try {
            String fileName = svgSavePanel.getSvgFileNameTextField().getText().trim();
            if (!fileName.toLowerCase().endsWith(".svg")) {
                fileName += ".svg";
            }
            File svgFile = Paths.get(resultDirectory, fileName).toFile();
            GraphicsToSVGExporter exporter = new GraphicsToSVGExporter(svgFile);
            int width = Integer.parseInt(svgSavePanel.getSvgWidthTextField().getText().trim());
            int height = Integer.parseInt(svgSavePanel.getSvgHeightTextField().getText().trim());
            Rectangle bounds = new Rectangle(0, 0, width, height);
            exporter.export(g2 -> {
                chart.draw(g2, bounds);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot save chart to SVG: " + e.getMessage());
        }
    }
}
