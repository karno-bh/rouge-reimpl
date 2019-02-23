package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.RLanguageHSDTestRunner;
import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.data.HSDTestLetterRepresentationRow;
import il.ac.sce.ir.metric.core.utils.converter.GraphicsToSVGExporter;
import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.Starter;
import il.ac.sce.ir.metric.starter.gui.main.element.SystemMetricSubMetricCheckBox;
import il.ac.sce.ir.metric.starter.gui.main.panel.applicative.utils.AnalyzePanelsCommons;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.SVGSavePanel;
import il.ac.sce.ir.metric.starter.gui.main.util.Caret;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import org.apache.xpath.operations.Bool;
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
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class AnalyzeBySystemPanel extends JPanel {

    private final String category;

    private final ResultsMetricHierarchyAnalyzer analyzer;

    private final String resultDirectory;

    private final JButton tableButton;

    private final JButton barChartButton;

    private final JButton statisticsButton;

    private final JButton notchedBoxButton;

    private final JCheckBox jitteredScatterPlotWithinNotchedBox;


    private final JCheckBox groupsInNotchedBoxesCheckBox;

    private final JCheckBox legendInNotchedBoxesCheckBox;

    private boolean scatterPlot;

    private boolean groupsInNotchedBoxes;

    private boolean legendInNotchedBoxes;

    private final Map<String, Map<String, Boolean>> selectedMetrics = new HashMap<>();

    private final Map<String, Boolean> selectedRougeMetrics = new HashMap<>();

    private final Map<String, Boolean> selectedRougeSubMetrics = new HashMap<>();

    private final Map<String, RLanguageHSDTestRunner> computedHSDCache =
            new HashMap<>();

    private final SVGSavePanel svgSavePanel = new SVGSavePanel();

    private final AnalyzePanelsCommons analyzePanelsCommons = new AnalyzePanelsCommons();

    public AnalyzeBySystemPanel(String category, ResultsMetricHierarchyAnalyzer analyzer, String resultDirectory) {
        this.category = category;
        this.analyzer = analyzer;
        this.resultDirectory = resultDirectory;
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

        statisticsButton = new JButton("Statistics");
        statisticsButton.addActionListener(this::onStatisticsButtonClicked);
        tableButtonConstraints.gridx = buttonsPanelX++;
        analyzeButtonsPanel.add(statisticsButton, tableButtonConstraints);

        notchedBoxButton = new JButton("Notched Boxes");
        notchedBoxButton.addActionListener(this::onNotchedBoxButtonClicked);
        // tableButtonConstraints = new GridBagConstraints();
        tableButtonConstraints.gridx = buttonsPanelX++;
        tableButtonConstraints.insets.right = 0;
        analyzeButtonsPanel.add(notchedBoxButton, tableButtonConstraints);

        jitteredScatterPlotWithinNotchedBox = new JCheckBox();
        groupsInNotchedBoxesCheckBox = new JCheckBox();
        legendInNotchedBoxesCheckBox = new JCheckBox();


        GridBagConstraints buttonsPanelConstraints = wholeSpaceFiller.getFillingConstraints();
        buttonsPanelConstraints.gridy = y++;
        add(createFunctionalPanels(), buttonsPanelConstraints);
        buttonsPanelConstraints.gridy = y++;
        add(analyzeButtonsPanel, buttonsPanelConstraints);

    }


    private JPanel createFunctionalPanels() {
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new GridBagLayout());
        WholeSpaceFiller filler = new WholeSpaceFiller();
        GridBagConstraints fillingConstraints = filler.getFillingConstraints();
        wrapperPanel.add(new JPanel(), fillingConstraints);
        GridBagConstraints functionalPanelConstraints = new GridBagConstraints();
        functionalPanelConstraints.fill = GridBagConstraints.BOTH;
        int x = 1;
        functionalPanelConstraints.gridx = x++;
        wrapperPanel.add(svgSavePanel, functionalPanelConstraints);

        functionalPanelConstraints.gridx = x++;
        wrapperPanel.add(createScatterPlotPanel(), functionalPanelConstraints);
        return wrapperPanel;
    }

    private JPanel createScatterPlotPanel() {
        JPanel scatterPlotPanel = new JPanel();
        scatterPlotPanel.setBorder(BorderFactory.createTitledBorder("Notched Box Configuration"));
        scatterPlotPanel.setLayout(new GridBagLayout());
        Caret caret = new Caret(0,0, 2);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(0, 10, 0,0);

        constraints = caret.asGridBag(constraints);
        scatterPlotPanel.add(new JLabel("Notched Box with Jittered Scatter Plot"), constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        jitteredScatterPlotWithinNotchedBox.addItemListener(this::onScatterPlotCheckBoxChanged);
        scatterPlotPanel.add(jitteredScatterPlotWithinNotchedBox, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        scatterPlotPanel.add(new JLabel("Groups in Notched Boxes"), constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        groupsInNotchedBoxesCheckBox.addItemListener(this::onGroupsInNotchedBoxesChanged);
        scatterPlotPanel.add(groupsInNotchedBoxesCheckBox, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        scatterPlotPanel.add(new JLabel("Full Metric Legend"), constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        legendInNotchedBoxesCheckBox.addItemListener(this::onLegendInNotchedBoxes);
        scatterPlotPanel.add(legendInNotchedBoxesCheckBox, constraints);


        WholeSpaceFiller filler = new WholeSpaceFiller();
        GridBagConstraints fillingConstraints = filler.getFillingConstraints();
        fillingConstraints.gridy = 1000;
        scatterPlotPanel.add(new JPanel(), fillingConstraints);

        return scatterPlotPanel;
    }


    private java.util.List<JPanel> constructMetricFilterPanel(Set<String> systems) {
        java.util.List<JPanel> metricPanels = new ArrayList<>();
        String nonVirtualSystem = null;
        for (String system : systems) {
            if (!Constants.VIRTUAL_TOPIC_SYSTEM.equals(system)) {
                nonVirtualSystem = system;
            }
        }
        Insets insets = new Insets(4,0, 0,4 );
        // assuming non virtual system has exactly the same metrics! (should be validated?)
        Set<String> availableSystemMetrics = new TreeSet<>(analyzer.getAvailableSystemMetrics(category, nonVirtualSystem));
        Set<String> rougeMetrics = new TreeSet<>();
        boolean hasAutoSummEng = false;
        for (String availableSystemMetric : availableSystemMetrics) {
            if (availableSystemMetric.startsWith(Constants.ROUGE_LOWER_CASE)) {
                rougeMetrics.add(availableSystemMetric);
                continue;
            } else if (availableSystemMetric.startsWith(Constants.AUTO_SUMM_ENG_LOWER_CASE)) {
                hasAutoSummEng = true;
                continue;
            }

            String displayMetric = Constants.DISPLAY_FILTER_ELENA_METRIC.keySet().contains(availableSystemMetric) ?
                    Constants.DISPLAY_FILTER_ELENA_METRIC.get(availableSystemMetric) : availableSystemMetric;
            TitledBorder titledBorder = BorderFactory.createTitledBorder(displayMetric);
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
            }

            metricPanels.add(availableSystemMetricPanel);
        }
        if (!rougeMetrics.isEmpty()) {
            JPanel rougeMetricsPanel = new JPanel();
            rougeMetricsPanel.setBorder(BorderFactory.createTitledBorder(Constants.ROUGE_LOWER_CASE));
            rougeMetricsPanel.setLayout(new GridBagLayout());
            WholeSpaceFiller filler = new WholeSpaceFiller();
            GridBagConstraints fillingConstraints = filler.getFillingConstraints();
            fillingConstraints.gridx = 1000;
            rougeMetricsPanel.add(new JPanel(), fillingConstraints);

            GridBagConstraints c = new GridBagConstraints();
            JPanel wrapperPanel = new JPanel();
            wrapperPanel.setLayout(new GridBagLayout());
            wrapperPanel.add(new JPanel(), fillingConstraints);
            int x = 0;
            c.gridy = 0;
            c.insets = insets;
            c.anchor = GridBagConstraints.EAST;
            for (String rougeMetric : rougeMetrics) {
                selectedRougeMetrics.put(rougeMetric, false);
                selectedMetrics.computeIfAbsent(rougeMetric, k -> new HashMap<>());
                String shortName = rougeMetric.substring(Constants.ROUGE_LOWER_CASE.length());
                JLabel rougeLabel = new JLabel(shortName.toUpperCase());
                c.gridx = x++;
                wrapperPanel.add(rougeLabel, c);

                SystemMetricSubMetricCheckBox rougeMetricCheckbox = new SystemMetricSubMetricCheckBox();
                rougeMetricCheckbox.setMetric(rougeMetric);
                rougeMetricCheckbox.addItemListener(this::onRougeMetricSelected);
                c.gridx = x++;
                wrapperPanel.add(rougeMetricCheckbox, c);
            }
            c.gridx = 0;
            rougeMetricsPanel.add(wrapperPanel, c);

            Set<String> availableSubMetrics = analyzer.getAvailableSubMetric(category, nonVirtualSystem, rougeMetrics.iterator().next());
            wrapperPanel = new JPanel();
            wrapperPanel.setLayout(new GridBagLayout());
            wrapperPanel.add(new JPanel(), fillingConstraints);
            c.gridx = x = 0;
            for (String availableSubMetric : availableSubMetrics) {
                selectedRougeSubMetrics.put(availableSubMetric, false);
                JLabel subMetricLabel = new JLabel(availableSubMetric);
                c.gridx = x++;
                wrapperPanel.add(subMetricLabel, c);

                SystemMetricSubMetricCheckBox rougeSubMetric = new SystemMetricSubMetricCheckBox();
                rougeSubMetric.setSubMetric(availableSubMetric);
                rougeSubMetric.addItemListener(this::onRougeSubMetricSelected);
                c.gridx = x++;
                wrapperPanel.add(rougeSubMetric, c);
            }
            c.gridx = 0;
            c.gridy = 1;
            rougeMetricsPanel.add(wrapperPanel, c);

            metricPanels.add(rougeMetricsPanel);

            clearRougeSelection();
        }

        if (hasAutoSummEng) {
            Set<String> availableSubMetrics = analyzer.getAvailableSubMetric(category, nonVirtualSystem, Constants.AUTO_SUMM_ENG_LOWER_CASE);
            Map<String, Boolean> subMetricsSelections = selectedMetrics.computeIfAbsent(Constants.AUTO_SUMM_ENG_LOWER_CASE, k -> new HashMap<>());
            availableSubMetrics.forEach(subMetric -> {
                subMetricsSelections.put(subMetric, false);
            });

            JPanel asePanel = new JPanel();
            asePanel.setLayout(new GridBagLayout());
            asePanel.setBorder(BorderFactory.createTitledBorder(Constants.AUTO_SUMM_ENG_LOWER_CASE));
            WholeSpaceFiller filler = new WholeSpaceFiller();
            GridBagConstraints fillingConstraints = filler.getFillingConstraints();
            fillingConstraints.gridx = 1000;
            asePanel.add(new JPanel(), fillingConstraints);

            GridBagConstraints innerConstraints = new GridBagConstraints();
            innerConstraints.fill = GridBagConstraints.HORIZONTAL;
            Caret asePanelCaret = new Caret(0,0,3);
            JPanel wordGraph = createAutoSummENGSubPanel("Word Graph", Constants.AUTO_SUMM_ENG_WORD_GRAPH_SUB_METRICS);
            asePanel.add(wordGraph, asePanelCaret.asGridBag(innerConstraints));

            asePanelCaret.next();
            JPanel wordHistogram = createAutoSummENGSubPanel("Word Histogram", Constants.AUTO_SUMM_ENG_WORD_HISTOGRAM_SUB_METRICS);
            asePanel.add(wordHistogram, asePanelCaret.asGridBag(innerConstraints));

            asePanelCaret.next();
            JPanel wordOverAll = createAutoSummENGSubPanel("Word Overall", new String[]{Constants.AUTO_SUMM_ENG_HISTO_OVERALL_SIMIL});
            asePanel.add(wordOverAll, asePanelCaret.asGridBag(innerConstraints));

            asePanelCaret.next();
            JPanel charGraph = createAutoSummENGSubPanel("Char Graph", Constants.AUTO_SUMM_ENG_CHAR_GRAPH_SUB_METRICS);
            asePanel.add(charGraph, asePanelCaret.asGridBag(innerConstraints));

            asePanelCaret.next();
            JPanel charHistogram = createAutoSummENGSubPanel("Char Histogram", Constants.AUTO_SUMM_ENG_CHAR_HISTOGRAM_SUB_METRICS);
            asePanel.add(charHistogram, asePanelCaret.asGridBag(innerConstraints));

            asePanelCaret.next();
            JPanel charOverAll = createAutoSummENGSubPanel("Char Overall", new String[]{Constants.AUTO_SUMM_ENG_N_HISTO_OVERALL_SIMIL});
            asePanel.add(charOverAll, asePanelCaret.asGridBag(innerConstraints));

            metricPanels.add(asePanel);
        }

        return metricPanels;
    }

    private JPanel createAutoSummENGSubPanel(String title, String[] subMetrics) {
        JPanel autoSummEngSubPanel = new JPanel();
        autoSummEngSubPanel.setLayout(new GridBagLayout());
        autoSummEngSubPanel.setBorder(BorderFactory.createTitledBorder(title));
        Insets insets = new Insets(4,0, 0,4 );
        WholeSpaceFiller filler = new WholeSpaceFiller();
        GridBagConstraints fillingConstraints = filler.getFillingConstraints();
        fillingConstraints.gridx = 0;
        autoSummEngSubPanel.add(new JPanel(), fillingConstraints);
        GridBagConstraints c = new GridBagConstraints();
        int x = 1;
        c.insets = insets;
        c.anchor = GridBagConstraints.EAST;
        for (String autoSumEngSubMetrics : subMetrics) {
            JLabel wordGraphLabel = new JLabel(autoSumEngSubMetrics);
            c.gridx = x++;
            autoSummEngSubPanel.add(wordGraphLabel, c);

            SystemMetricSubMetricCheckBox checkBox = new SystemMetricSubMetricCheckBox();
            checkBox.setMetric(Constants.AUTO_SUMM_ENG_LOWER_CASE);
            checkBox.setSubMetric(autoSumEngSubMetrics);
            checkBox.addItemListener(this::onCheckBoxChanged);
            c.gridx = x++;
            autoSummEngSubPanel.add(checkBox, c);
        }
        return autoSummEngSubPanel;
    }

    private void onCheckBoxChanged(ItemEvent event) {
        SystemMetricSubMetricCheckBox source = (SystemMetricSubMetricCheckBox)event.getSource();
        // System.out.println("Metric: " + source.getMetric() + " Sub Metric: " + source.getSubMetric());
        Map<String, Boolean> subMetrics = selectedMetrics.get(source.getMetric());
        subMetrics.put(source.getSubMetric(), event.getStateChange() == ItemEvent.SELECTED);
    }

    private void onScatterPlotCheckBoxChanged(ItemEvent event) {
        scatterPlot = event.getStateChange() == ItemEvent.SELECTED;
    }

    private void onGroupsInNotchedBoxesChanged(ItemEvent event){
        groupsInNotchedBoxes = event.getStateChange() == ItemEvent.SELECTED;
    }

    private void onLegendInNotchedBoxes(ItemEvent event) {
        legendInNotchedBoxes = event.getStateChange() == ItemEvent.SELECTED;
    }

    private void onRougeMetricSelected(ItemEvent event) {
        SystemMetricSubMetricCheckBox source = (SystemMetricSubMetricCheckBox)event.getSource();
        selectedRougeMetrics.put(source.getMetric(), event.getStateChange() == ItemEvent.SELECTED);
        syncRougeWithSelectedMetrics();
    }

    private void onRougeSubMetricSelected(ItemEvent event) {
        SystemMetricSubMetricCheckBox source = (SystemMetricSubMetricCheckBox)event.getSource();
        selectedRougeSubMetrics.put(source.getSubMetric(), event.getStateChange() == ItemEvent.SELECTED);
        syncRougeWithSelectedMetrics();
    }

    private void clearRougeSelection() {
        selectedRougeMetrics.forEach((metric,metricSelected) -> {
            selectedRougeSubMetrics.forEach((subMetric, subMetricSelected) -> {
                selectedMetrics.get(metric).put(subMetric, false);
            });
        });
    }
    private void syncRougeWithSelectedMetrics() {
        clearRougeSelection();
        selectedRougeMetrics.forEach((metric,metricSelected) -> {
            if (metricSelected != null && metricSelected) {
                selectedRougeSubMetrics.forEach((subMetric, subMetricSelected) -> {
                    selectedMetrics.get(metric).put(subMetric, subMetricSelected);
                });
            }
        });
    }

    private void onTableButtonClicked(ActionEvent event) {
        if (!analyzePanelsCommons.hasSelection(selectedMetrics, this)) {
            return;
        }
        Table table = analyzer.asAverageTable(category, selectedMetrics);
        analyzePanelsCommons.filterTableDisplaying(table);
        JDialog jDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Table View");
        JTable systemsSummary = new JTable(table);
        systemsSummary.setFillsViewportHeight(true);
        systemsSummary.setAutoCreateRowSorter(true);
        systemsSummary.setPreferredScrollableViewportSize(new Dimension(800, 600));
        jDialog.add(new JScrollPane(systemsSummary));
        jDialog.pack();
        jDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        jDialog.setVisible(true);
    }

    private void onBarChartButtonClicked(ActionEvent event) {
        if (!analyzePanelsCommons.hasSelection(selectedMetrics, this)) {
            return;
        }
        JDialog jDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Bar Chart View");
        Table table = analyzer.asAverageTable(category, selectedMetrics);
        analyzePanelsCommons.filterTableDisplaying(table);
        CategoryDataset categoryDataset = analyzer.tableToCategoryDataSet(table);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Average Metric Values By System",
                "Metric",
                "Metric Value",
                categoryDataset,
                PlotOrientation.VERTICAL,
                true, true, false);


        if (svgSavePanel.isSaveToSVG()) {
            analyzePanelsCommons.exportChartAsSVG(barChart, svgSavePanel, resultDirectory);
        }
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        jDialog.add(chartPanel);
        jDialog.pack();
        jDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        jDialog.setVisible(true);
    }

    private void onStatisticsButtonClicked(ActionEvent event) {
        if (!analyzePanelsCommons.hasSelection(selectedMetrics, this)) {
            return;
        }
        JDialog jDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Statistics");
        JTextArea textArea = new JTextArea(48, 160);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        Map<String, List<Double>> flattenedData = analyzer.asFlattenedData(category, selectedMetrics);
        RLanguageHSDTestRunner results = computedHSDCache.computeIfAbsent(selectedMetricsToCacheKey(), k -> {
            RLanguageHSDTestRunner runner = new RLanguageHSDTestRunner(flattenedData);
            runner.process();
            return runner;
        });
        Map<String, String> statisticTables = results.getStatisticTables();
        StringBuilder sb = new StringBuilder(512);
        statisticTables.forEach((tableName, table) -> {
            sb.append(tableName).append(":\n\n").append(table).append("\n\n");
        });
        textArea.setText(sb.toString());
        jDialog.add(new JScrollPane(textArea));
        jDialog.pack();
        jDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        jDialog.setVisible(true);
    }

    private void onNotchedBoxButtonClicked(ActionEvent event) {
        if (!analyzePanelsCommons.hasSelection(selectedMetrics, this)) {
            return;
        }
        Map<String, List<Double>> flattenedData = analyzer.asFlattenedData(category, selectedMetrics);
        Map<String, List<Double>> notchedBoxFlattenedData;
        Map<String, String> metricGroups = new HashMap<>();
        if (groupsInNotchedBoxes) {
            RLanguageHSDTestRunner results = computedHSDCache.computeIfAbsent(selectedMetricsToCacheKey(), k -> {
                RLanguageHSDTestRunner runner = new RLanguageHSDTestRunner(flattenedData);
                runner.process();
                return runner;
            });
            notchedBoxFlattenedData = new LinkedHashMap<>();
            for (HSDTestLetterRepresentationRow hsdTestLetterRepresentationRow : results.getHsdLetterRepresentation()) {
                String metric = hsdTestLetterRepresentationRow.getMetric();
                notchedBoxFlattenedData.put(metric, flattenedData.get(metric));
                metricGroups.put(metric, hsdTestLetterRepresentationRow.getGroups());
            }
        } else {
            notchedBoxFlattenedData = flattenedData;
        }
        // System.out.println(result);


        AnalyzeDialogNotchedBoxPanel innerDialogPanel = new AnalyzeDialogNotchedBoxPanel(
                notchedBoxFlattenedData,
                metricGroups,
                scatterPlot,
                legendInNotchedBoxes);
        if (svgSavePanel.isSaveToSVG()) {
            innerDialogPanel.exportChartAsSVG(
                    resultDirectory,
                    svgSavePanel.getSvgFileNameTextField().getText(),
                    svgSavePanel.getSvgWidthTextField().getText(),
                    svgSavePanel.getSvgHeightTextField().getText()
            );
        }
        JDialog jDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Notched Boxes View");
        jDialog.add(innerDialogPanel);
        jDialog.pack();
        jDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        jDialog.setVisible(true);
    }



    /**
     * Map as a key in another map produces unexpected result. Thus, map is reduced to a string
     * @return reduced selected metric to string key
     */
    private String selectedMetricsToCacheKey() {
        StringBuilder sb = new StringBuilder(256);
        new TreeSet<>(selectedMetrics.keySet()).forEach(metric -> {
            Map<String, Boolean> subMetrics = selectedMetrics.get(metric);
            new TreeSet<>(subMetrics.keySet()).forEach(subMetric -> {
                Boolean selected = subMetrics.get(subMetric);
                if (selected != null && selected) {
                    sb.append(metric).append('/').append(subMetric);
                }
                sb.append('/');
            });
        });
        return sb.toString();
    }

}
