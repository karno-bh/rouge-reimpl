package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.gui.NotchedBoxGraph;
import il.ac.sce.ir.metric.core.gui.data.MultiNotchedBoxData;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AnalyzeDialogNotchedBoxPanel extends JPanel {

    private final Map<String, List<Double>> flattenedData;

    public AnalyzeDialogNotchedBoxPanel(Map<String, List<Double>> flattenedData, boolean jitteredScatterPlot) {
        Objects.requireNonNull(flattenedData, "Flattened data cannot be null");
        this.flattenedData = flattenedData;

        setLayout(new GridBagLayout());
        Map<String, String> flattenedDataLegend = new HashMap<>();
        Table table = new Table();
        List<String> header = Arrays.asList("Metric", "Label");
        table.setColumns(header);
        int j = 1;
        for (String flattenedDataKey : flattenedData.keySet()) {
            List<Object> tableRow = new ArrayList<>();
            tableRow.add(flattenedDataKey);
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
            multiNotchedBoxData.add(flattenedDataLegend.get(compoundKey), asDoubleArr);
        });

        NotchedBoxGraph notchedBoxGraph = new NotchedBoxGraph();
        notchedBoxGraph.setJitteredScatterPlot(jitteredScatterPlot);
        notchedBoxGraph.setGraphName("Metrics Notched Boxes");
        notchedBoxGraph.setMultiNotchedBoxData(multiNotchedBoxData);

        WholeSpaceFiller wholeSpaceFiller = new WholeSpaceFiller();
        GridBagConstraints notchBoxConstraints = wholeSpaceFiller.getFillingConstraints();
        add(notchedBoxGraph, notchBoxConstraints);

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
