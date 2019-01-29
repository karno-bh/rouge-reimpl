package il.ac.sce.ir.metric.temp_playing;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

public class MyPlayingWithBarChart {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("My Playing With Bar Chart");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(createChartPanel());

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }

    static JPanel createChartPanel() {
        JFreeChart barChart = ChartFactory.createBarChart(
                "Rouge Average Precision",
                "Metric",
                "Precision",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize( new Dimension(800, 600));
        return chartPanel;
    }

    static CategoryDataset createDataset() {

        String sys01 =  "System 01";
        String sys02 =  "System 02";
        String sys03 =  "System 03";
        String sys04 =  "System 04";
        String sys05 =  "System 05";
        String sys06 =  "System 06";
        String sys07 =  "System 07";

        String rougeLPrecision = "RougeL Precision";
        String rougnN2Precision = "Rouge N 3 Precision";
        String rougeSRecall= "Rouge S Recall";

        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();

        defaultCategoryDataset.addValue(0.34, rougeLPrecision, sys01);
        defaultCategoryDataset.addValue(0.56, rougnN2Precision, sys01);
        defaultCategoryDataset.addValue(0.25, rougeSRecall, sys01);

        defaultCategoryDataset.addValue(0.12, rougeLPrecision, sys02);
        defaultCategoryDataset.addValue(0.44, rougnN2Precision, sys02);
        defaultCategoryDataset.addValue(0.45, rougeSRecall, sys02);

        defaultCategoryDataset.addValue(0.66, rougeLPrecision, sys03);
        defaultCategoryDataset.addValue(0.8, rougnN2Precision, sys03);
        defaultCategoryDataset.addValue(0.12, rougeSRecall, sys03);

        defaultCategoryDataset.addValue(0.66, rougeLPrecision, sys04);
        defaultCategoryDataset.addValue(0.8, rougnN2Precision, sys04);
        defaultCategoryDataset.addValue(0.12, rougeSRecall, sys04);

        defaultCategoryDataset.addValue(0.66, rougeLPrecision, sys05);
        defaultCategoryDataset.addValue(0.8, rougnN2Precision, sys05);
        defaultCategoryDataset.addValue(0.12, rougeSRecall, sys05);

        defaultCategoryDataset.addValue(0.66, rougeLPrecision, sys06);
        defaultCategoryDataset.addValue(0.8, rougnN2Precision, sys06);
        defaultCategoryDataset.addValue(0.12, rougeSRecall, sys06);

        defaultCategoryDataset.addValue(0.66, rougeLPrecision, sys07);
        defaultCategoryDataset.addValue(0.8, rougnN2Precision, sys07);
        defaultCategoryDataset.addValue(0.12, rougeSRecall, sys07);




        return defaultCategoryDataset;
    }
}
