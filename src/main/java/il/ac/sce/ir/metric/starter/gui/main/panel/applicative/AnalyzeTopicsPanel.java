package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import il.ac.sce.ir.metric.core.config.Constants;
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
import salvo.jesus.graph.visual.layout.Grid;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.*;
import java.util.List;

public class AnalyzeTopicsPanel extends JPanel {

    private final String category;

    private final ResultsMetricHierarchyAnalyzer analyzer;

    private final Map<String, Boolean> selectedSystems = new HashMap<>();

    private final Map<String, Map<String, Boolean>> selectedMetric = new HashMap<>();

    private final JButton table;

    private final JButton barChart;

    public AnalyzeTopicsPanel(String category, ResultsMetricHierarchyAnalyzer analyzer) {
        this.category = category;
        this.analyzer = analyzer;

        setLayout(new GridBagLayout());
        TitledBorder topics = BorderFactory.createTitledBorder("Topics");
        setBorder(topics);
        Map<String, Set<String>> allAvailableSystems = analyzer.getAvailableSystems();
        Set<String> sortedSystems = new TreeSet<>(allAvailableSystems.get(category));
        JPanel systemsPanel = new JPanel();
        systemsPanel.setLayout(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("System");
        systemsPanel.setBorder(border);
        WholeSpaceFiller filler = new WholeSpaceFiller();
        GridBagConstraints systemPanelSpringConstraints = filler.getFillingConstraints();
        systemPanelSpringConstraints.gridx = 1000;
        systemsPanel.add(new JPanel(), systemPanelSpringConstraints);
        Caret caret = new Caret(0,0, 10);
        Insets insets = new Insets(4,0, 0,4 );
        for (String system : sortedSystems) {
            if (Constants.VIRTUAL_TOPIC_SYSTEM.equals(system)) {
                continue;
            }
            for (int i = 0; i < 2; i++) {
                int x = caret.getX(), y = caret.getY();
                JComponent comp;
                if (x % 2 == 0) {
                    comp = new JLabel(system);
                } else {
                    SystemMetricSubMetricCheckBox checkBox = new SystemMetricSubMetricCheckBox();
                    checkBox.setSystem(system);
                    checkBox.addItemListener(this::onSystemCheckBoxClicked);
                    selectedSystems.put(system, false);
                    comp = checkBox;
                }
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = x;
                constraints.gridy = y;
                constraints.insets = insets;
                constraints.anchor = GridBagConstraints.EAST;
                systemsPanel.add(comp, constraints);
                caret.next();
            }
        }
        int y = 0;
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = y++;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        add(systemsPanel, constraints);

        Map<String, List<String>> topicSubMetrics = analyzer.getVirtualSTopicSystemMetrics(category);
        List<JPanel> metricPanels = getMetricPanels(topicSubMetrics);
        for (JPanel metricPanel : metricPanels) {
            constraints.gridy = y++;
            add(metricPanel, constraints);
        }


        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints buttonsPanelSpringConstraints = filler.getFillingConstraints();
        buttonsPanel.add(new JPanel(), buttonsPanelSpringConstraints);
        int buttonsX = 1;

        Insets buttonInsets = new Insets(20,0,0,10);
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        buttonsConstraints.gridx = buttonsX++;
        buttonsConstraints.insets = buttonInsets;
        table = new JButton("Table");
        table.addActionListener(this::onTableClicked);
        buttonsPanel.add(table, buttonsConstraints);

        buttonsConstraints.gridx = buttonsX++;
        buttonsConstraints.insets.right = 0;
        barChart = new JButton("Topic vs. System Summary");
        barChart.addActionListener(this::onBarChartClicked);
        buttonsPanel.add(barChart, buttonsConstraints);

        /*buttonsConstraints.gridx = buttonsX++;
        buttonsConstraints.insets.right = 0;
        topicNotchedBoxes = new JButton("Topic Notched Boxes");
        topicNotchedBoxes.addActionListener(this::onNotchedBoxClicked);
        buttonsPanel.add(topicNotchedBoxes, buttonsConstraints);*/

        constraints.gridy = y++;
        add(buttonsPanel, constraints);
    }

    private void onSystemCheckBoxClicked(ItemEvent itemEvent) {
        SystemMetricSubMetricCheckBox checkBox = (SystemMetricSubMetricCheckBox) itemEvent.getSource();
        selectedSystems.put(checkBox.getSystem(), itemEvent.getStateChange() == ItemEvent.SELECTED);
    }

    private void onSubMetricCheckBoxClicked(ItemEvent itemEvent) {
        SystemMetricSubMetricCheckBox checkBox = (SystemMetricSubMetricCheckBox) itemEvent.getSource();
        Map<String, Boolean> subMetrics = selectedMetric.get(checkBox.getMetric());
        subMetrics.put(checkBox.getSubMetric(), itemEvent.getStateChange() == ItemEvent.SELECTED);
    }

    private void onTableClicked(ActionEvent event) {
        Table tableData = analyzer.asTopicTable(category, selectedSystems, selectedMetric);
        JDialog jDialog = new JDialog(Starter.getTopLevelFrame(), "Table View");
        JTable systemsSummary = new JTable(tableData);
        systemsSummary.setFillsViewportHeight(true);
        systemsSummary.setAutoCreateRowSorter(true);
        systemsSummary.setPreferredScrollableViewportSize(new Dimension(800, 600));
        jDialog.add(new JScrollPane(systemsSummary));
        jDialog.pack();
        jDialog.setLocationRelativeTo(Starter.getTopLevelFrame());
        jDialog.setVisible(true);
    }

    private void onBarChartClicked(ActionEvent event) {
        JDialog jDialog = new JDialog(Starter.getTopLevelFrame(), "Bar Chart View");
        Table tableData = analyzer.asTopicTable(category, selectedSystems, selectedMetric);
        CategoryDataset categoryDataset = analyzer.tableToCategoryDataSet(tableData);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Topic Average vs Summary Value",
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

    private void onNotchedBoxClicked(ActionEvent event) {

    }

    private List<JPanel> getMetricPanels(Map<String, List<String>> topicSubMetrics) {
        List<JPanel> metricPanels = new ArrayList<>();
        topicSubMetrics.forEach((metricKey, subMetrics) -> {
            JPanel metricPanel = new JPanel();
            metricPanel.setLayout(new GridBagLayout());
            metricPanel.setBorder(BorderFactory.createTitledBorder(metricKey));
            WholeSpaceFiller spaceFiller = new WholeSpaceFiller();
            GridBagConstraints metricPanelSpringConstraints = spaceFiller.getFillingConstraints();
            metricPanelSpringConstraints.gridx = 1000;
            metricPanel.add(new JPanel(), metricPanelSpringConstraints);

            Insets insets = new Insets(4,0, 0,4 );
            Caret caret = new Caret(0,0, 10);
            for (String subMetric : subMetrics) {
                for (int i = 0; i < 2; i++) {
                    int x = caret.getX(), y = caret.getY();
                    JComponent comp;
                    if (x % 2 == 0) {
                        comp = new JLabel(subMetric);
                    } else {
                        SystemMetricSubMetricCheckBox checkBox = new SystemMetricSubMetricCheckBox();
                        checkBox.setMetric(metricKey);
                        checkBox.setSubMetric(subMetric);
                        checkBox.addItemListener(this::onSubMetricCheckBoxClicked);
                        // selectedSystems.put(system, false);
                        Map<String, Boolean> subMetricSelections = selectedMetric.computeIfAbsent(metricKey, k -> new HashMap<>());
                        subMetricSelections.put(subMetric, false);
                        comp = checkBox;
                    }
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridx = x;
                    constraints.gridy = y;
                    constraints.insets = insets;
                    constraints.anchor = GridBagConstraints.EAST;
                    metricPanel.add(comp, constraints);
                    caret.next();
                }
            }
            metricPanels.add(metricPanel);
        });
        return metricPanels;
    }
}
