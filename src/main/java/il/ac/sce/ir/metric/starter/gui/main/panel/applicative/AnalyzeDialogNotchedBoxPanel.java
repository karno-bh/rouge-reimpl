package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.NotchedBoxGraph;
import il.ac.sce.ir.metric.core.gui.data.MultiNotchedBoxData;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.core.utils.converter.GraphicsToSVGExporter;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class AnalyzeDialogNotchedBoxPanel extends JPanel {

    private final Map<String, List<Double>> flattenedData;

    private final Map<String, String> metricGroups;

    private final NotchedBoxGraph notchedBoxGraph;

    public AnalyzeDialogNotchedBoxPanel(Map<String, List<Double>> flattenedData,
                                        Map<String, String> metricGroups,
                                        boolean jitteredScatterPlot,
                                        boolean showLegend) {
        Objects.requireNonNull(flattenedData, "Flattened data cannot be null");
        Objects.requireNonNull(metricGroups, "Metric Groups cannot be null");
        this.flattenedData = flattenedData;
        this.metricGroups = metricGroups;
        setLayout(new GridBagLayout());
        Map<String, String> flattenedDataLegend = new HashMap<>();
        Table table = new Table();
        List<String> header = Arrays.asList("Metric", "Label");
        table.setColumns(header);
        int j = 1;
        for (String flattenedDataKey : flattenedData.keySet()) {
            List<Object> tableRow = new ArrayList<>();
            String displayFlattenedDataKey = flattenedDataKey.replace(Constants.ELENA_READABILITY_LOWER_CASE, Constants.READABILITY_LOWER_CASE);
            tableRow.add(displayFlattenedDataKey);
            tableRow.add(j);
            table.addRow(tableRow);

            flattenedDataLegend.put(flattenedDataKey, "(" + j++ + ")");
        }

        MultiNotchedBoxData multiNotchedBoxData = new MultiNotchedBoxData(true);
        flattenedData.forEach((compoundKey,values) -> {
            double[] asDoubleArr = new double[values.size()];
            for(int i = 0; i < asDoubleArr.length; i++) {
                asDoubleArr[i] = values.get(i);
            }
            String showValue = showLegend ?
                    flattenedDataLegend.get(compoundKey) : compoundKey.substring(0, compoundKey.indexOf("/"));
            multiNotchedBoxData.add(compoundKey, asDoubleArr, metricGroups.get(compoundKey), showValue);
        });

        notchedBoxGraph = new NotchedBoxGraph();
        notchedBoxGraph.setJitteredScatterPlot(jitteredScatterPlot);
        notchedBoxGraph.setGraphName("Metrics Notched Boxes");
        notchedBoxGraph.setMultiNotchedBoxData(multiNotchedBoxData);

        WholeSpaceFiller wholeSpaceFiller = new WholeSpaceFiller();
        GridBagConstraints notchBoxConstraints = wholeSpaceFiller.getFillingConstraints();
        add(notchedBoxGraph, notchBoxConstraints);

        if (showLegend) {
            JTable legendTable = new JTable(table);
            legendTable.setFillsViewportHeight(true);
            legendTable.setAutoCreateRowSorter(true);
            legendTable.setPreferredScrollableViewportSize(new Dimension(600, 200));

            GridBagConstraints legendConstraints  = new GridBagConstraints();
            legendConstraints.gridy = 1;
            legendConstraints.fill = GridBagConstraints.BOTH;
            add(new JScrollPane(legendTable), legendConstraints);
        }
    }

    public void exportChartAsSVG(String resultDirectory, String fileNameRaw, String widthRaw, String heightRaw) {
        try {
            String fileName = fileNameRaw.trim();
            if (!fileName.toLowerCase().endsWith(Constants.SVG_EXTENSION)) {
                fileName += Constants.SVG_EXTENSION;
            }
            File svgFile = Paths.get(resultDirectory, fileName).toFile();
            GraphicsToSVGExporter exporter = new GraphicsToSVGExporter(svgFile);
            int width = Integer.parseInt(widthRaw.trim());
            int height = Integer.parseInt(heightRaw.trim());
            notchedBoxGraph.setPeerLessPaint(true);
            notchedBoxGraph.setPeerLessWidth(width);
            notchedBoxGraph.setPeerLessHeight(height);
            exporter.export(notchedBoxGraph::drawAll);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot save notched box to SVG: " + e.getMessage());
        } finally {
            notchedBoxGraph.setPeerLessPaint(false);
        }
    }
}
