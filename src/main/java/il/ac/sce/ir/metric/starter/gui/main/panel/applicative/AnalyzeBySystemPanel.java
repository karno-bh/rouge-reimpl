package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.NotchedBoxGraph;
import il.ac.sce.ir.metric.core.gui.data.MultiNotchedBoxData;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.Starter;
import il.ac.sce.ir.metric.starter.gui.main.element.SystemMetricSubMetricCheckBox;
import il.ac.sce.ir.metric.starter.gui.main.util.Caret;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.*;
import java.util.List;

public class AnalyzeBySystemPanel extends JPanel {

    private final String category;

    private final ResultsMetricHierarchyAnalyzer analyzer;

    private final JButton tableButton;

    private final JButton barChartButton;

    private final JButton notchedBoxButton;

    private final Map<String, Map<String, Boolean>> selectedMetrics = new HashMap<>();

    public AnalyzeBySystemPanel(String category, ResultsMetricHierarchyAnalyzer analyzer) {
        this.category = category;
        this.analyzer = analyzer;
        TitledBorder titledBorder = BorderFactory.createTitledBorder("System");
        setBorder(titledBorder);
        setLayout(new GridBagLayout());
        int y = 0;
        Map<String, Set<String>> availableSystems = analyzer.getAvailableSystems();
        Set<String> systems = availableSystems.get(category);
        GridBagConstraints systemPanelConstraints = new GridBagConstraints();
        systemPanelConstraints.weightx = 1;
        systemPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        for (JPanel metricPanel : constructMetricFilterPanel(systems)) {
            systemPanelConstraints.gridy = y++;
            add(metricPanel, systemPanelConstraints);
        }
        y = 1000;
        JPanel analyzeButtonsPanel = new JPanel();
        analyzeButtonsPanel.setLayout(new GridBagLayout());
        WholeSpaceFiller wholeSpaceFiller = new WholeSpaceFiller();
        GridBagConstraints buttonsPanelSpringConstraints = wholeSpaceFiller.getFillingConstraints();
        JPanel buttonsPanelSpring = new JPanel();
        analyzeButtonsPanel.add(buttonsPanelSpring, buttonsPanelSpringConstraints);
        int buttonsPanelX = 1;

        Insets buttonInsets = new Insets(20,0,0,10);

        GridBagConstraints tableButtonConstraints = new GridBagConstraints();
        tableButtonConstraints.gridx = buttonsPanelX++;
        tableButtonConstraints.insets = buttonInsets;

        tableButton = new JButton("Table");
        tableButton.addActionListener(this::onTableButtonClicked);
        analyzeButtonsPanel.add(tableButton, tableButtonConstraints);

        barChartButton = new JButton("Bar Chart");
        barChartButton.addActionListener(this::onBarChartButtonClicked);
        tableButtonConstraints.gridx = buttonsPanelX++;
        analyzeButtonsPanel.add(barChartButton, tableButtonConstraints);

        notchedBoxButton = new JButton("Notched Boxes");
        notchedBoxButton.addActionListener(this::onNotchedBoxButtonClicked);
        // tableButtonConstraints = new GridBagConstraints();
        tableButtonConstraints.gridx = buttonsPanelX++;
        tableButtonConstraints.insets.right = 0;
        analyzeButtonsPanel.add(notchedBoxButton, tableButtonConstraints);

        GridBagConstraints buttonsPanelConstraints = wholeSpaceFiller.getFillingConstraints();
        buttonsPanelConstraints.gridy = y++;
        add(analyzeButtonsPanel, buttonsPanelConstraints);


    }

    private java.util.List<JPanel> constructMetricFilterPanel(Set<String> systems) {
        java.util.List<JPanel> metricPanels = new ArrayList<>();
        String nonVirtualSystem = null;
        for (String system : systems) {
            if (!Constants.VIRTUAL_TOPIC_SYSTEM.equals(system)) {
                nonVirtualSystem = system;
            }
        }
        // assuming non virtual system has exactly the same metrics! (should be validated?)
        Set<String> availableSystemMetrics = new TreeSet<>(analyzer.getAvailableSystemMetrics(category, nonVirtualSystem));
        for (String availableSystemMetric : availableSystemMetrics) {
            TitledBorder titledBorder = BorderFactory.createTitledBorder(availableSystemMetric);
            JPanel availableSystemMetricPanel = new JPanel();
            availableSystemMetricPanel.setBorder(titledBorder);
            availableSystemMetricPanel.setLayout(new GridBagLayout());
            JPanel availableSystemMetricPanelSpring = new JPanel();
            WholeSpaceFiller wholeSpaceFiller = new WholeSpaceFiller();
            GridBagConstraints springConstraints = wholeSpaceFiller.getFillingConstraints();
            springConstraints.gridx = 1000;
            availableSystemMetricPanel.add(availableSystemMetricPanelSpring, springConstraints);
            Set<String> availableSubMetrics = analyzer.getAvailableSubMetric(category, nonVirtualSystem, availableSystemMetric);
            Caret caret = new Caret(0,0,10);
            Insets insets = new Insets(4,0, 0,4 );
            for (String availableSubMetric : availableSubMetrics) {
                // System.out.println("availableSystemMetric: " + availableSystemMetric + " availableSubMetric: " + availableSubMetric);
                for (int i = 0; i < 2; i++) {
                    int x = caret.getX(), y = caret.getY();
                    JComponent comp;
                    if (x % 2 == 0) {
                        comp = new JLabel(availableSubMetric);
                    } else {
                        SystemMetricSubMetricCheckBox checkBox = new SystemMetricSubMetricCheckBox();
                        checkBox.setMetric(availableSystemMetric);
                        checkBox.setSubMetric(availableSubMetric);
                        checkBox.addItemListener(this::onCheckBoxChanged);
                        Map<String, Boolean> subMetricValues = selectedMetrics.computeIfAbsent(availableSystemMetric, k -> new HashMap<>());
                        subMetricValues.put(availableSubMetric, false);
                        comp = checkBox;
                    }
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridx = x;
                    constraints.gridy = y;
                    constraints.insets = insets;
                    constraints.anchor = GridBagConstraints.EAST;
                    availableSystemMetricPanel.add(comp, constraints);
                    caret.next();
                }
                // availableSystemMetricPanel.add(new JLabel(availableSubMetric));
            }
            metricPanels.add(availableSystemMetricPanel);
        }

        return metricPanels;
    }

    private void onCheckBoxChanged(ItemEvent event) {
        SystemMetricSubMetricCheckBox source = (SystemMetricSubMetricCheckBox)event.getSource();
        // System.out.println("Metric: " + source.getMetric() + " Sub Metric: " + source.getSubMetric());
        Map<String, Boolean> subMetrics = selectedMetrics.get(source.getMetric());
        subMetrics.put(source.getSubMetric(), event.getStateChange() == ItemEvent.SELECTED);
    }

    private void onTableButtonClicked(ActionEvent event) {
        /*ObjectMapper objectMapper = new ObjectMapper();
        try {
            String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(analyzer.getOriginalMetricHierarchy());
            System.out.println(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

        Table table = analyzer.asAverageTable(category, selectedMetrics);
        JDialog jDialog = new JDialog(Starter.getTopLevelFrame(), "Table View");
        JTable systemsSummary = new JTable(table);
        systemsSummary.setFillsViewportHeight(true);
        systemsSummary.setAutoCreateRowSorter(true);
        systemsSummary.setPreferredScrollableViewportSize(new Dimension(800, 600));
        jDialog.add(new JScrollPane(systemsSummary));
        jDialog.pack();
        jDialog.setLocationRelativeTo(Starter.getTopLevelFrame());
        jDialog.setVisible(true);
    }

    private void onBarChartButtonClicked(ActionEvent event) {
        JDialog jDialog = new JDialog(Starter.getTopLevelFrame(), "Bar Chart View");
        Table table = analyzer.asAverageTable(category, selectedMetrics);
        CategoryDataset categoryDataset = analyzer.tableToCategoryDataSet(table);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Average Metric Values By System",
                "Metric",
                "Metric Value",
                categoryDataset,
                PlotOrientation.VERTICAL,
                true, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        jDialog.add(chartPanel);
        jDialog.pack();
        jDialog.setLocationRelativeTo(Starter.getTopLevelFrame());
        jDialog.setVisible(true);
    }

    private void onNotchedBoxButtonClicked(ActionEvent event) {
        Map<String, List<Double>> flattenedData = analyzer.asFlattenedData(category, selectedMetrics);
        JPanel innerDialogPanel = new AnalyzeDialogNotchedBoxPanel(flattenedData);

        JDialog jDialog = new JDialog(Starter.getTopLevelFrame(), "Notched Boxes View");
        jDialog.add(innerDialogPanel);
        jDialog.pack();
        jDialog.setLocationRelativeTo(Starter.getTopLevelFrame());
        jDialog.setVisible(true);
    }

}
