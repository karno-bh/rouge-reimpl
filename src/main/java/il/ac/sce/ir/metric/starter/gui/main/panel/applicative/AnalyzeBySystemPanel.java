package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.RLanguageHSDTestRunner;
import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.data.HSDTestLetterRepresentationRow;
import il.ac.sce.ir.metric.core.utils.converter.GraphicsToSVGExporter;
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

    private final JButton notchedBoxButton;

    private final JCheckBox jitteredScatterPlotWithinNotchedBox;

    private final JLabel svgFileNameLabel = new JLabel("SVG File Name");

    private final JLabel widthLabel = new JLabel("Width");

    private final JLabel heightLabel = new JLabel("Height");

    private final JTextField svgFileNameTextField;

    private final JTextField svgWidthTextField;

    private final JTextField svgHeightTextField;

    private List<JComponent> svgRelatedComponents;

    private final JCheckBox saveToSVGCheckBox;

    private final JCheckBox groupsInNotchedBoxesCheckBox;

    private final JCheckBox legendInNotchedBoxesCheckBox;

    private boolean scatterPlot;

    private boolean groupsInNotchedBoxes;

    private boolean legendInNotchedBoxes;

    private boolean saveToSVG;

    private final Map<String, Map<String, Boolean>> selectedMetrics = new HashMap<>();

    private final Map<String, List<HSDTestLetterRepresentationRow>> computedHSDCache =
            new HashMap<>();


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

        notchedBoxButton = new JButton("Notched Boxes");
        notchedBoxButton.addActionListener(this::onNotchedBoxButtonClicked);
        // tableButtonConstraints = new GridBagConstraints();
        tableButtonConstraints.gridx = buttonsPanelX++;
        tableButtonConstraints.insets.right = 0;
        analyzeButtonsPanel.add(notchedBoxButton, tableButtonConstraints);

        jitteredScatterPlotWithinNotchedBox = new JCheckBox();
        groupsInNotchedBoxesCheckBox = new JCheckBox();
        legendInNotchedBoxesCheckBox = new JCheckBox();
        // JPanel scatterPlotPanel = createScatterPlotPanel();

        svgFileNameTextField = new JTextField();
        saveToSVGCheckBox = new JCheckBox();
        svgWidthTextField = new JTextField();
        svgHeightTextField = new JTextField();
        svgRelatedComponents = Arrays.asList(
                svgFileNameLabel, widthLabel, heightLabel,
                svgFileNameTextField, svgWidthTextField, svgHeightTextField
        );
        svgRelatedComponents.forEach(c -> c.setEnabled(false));
        // JPanel svgSavePanel = createSVGSavePanel();

        GridBagConstraints buttonsPanelConstraints = wholeSpaceFiller.getFillingConstraints();
        buttonsPanelConstraints.gridy = y++;
        add(createFunctionalPanels(), buttonsPanelConstraints);
        /*buttonsPanelConstraints.gridy = y++;
        add(svgSavePanel, buttonsPanelConstraints);
        buttonsPanelConstraints.gridy = y++;
        add(scatterPlotPanel, buttonsPanelConstraints);*/
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
        wrapperPanel.add(createSVGSavePanel(), functionalPanelConstraints);

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

    private JPanel createSVGSavePanel() {
        JPanel svgSavePanel = new JPanel();
        svgSavePanel.setBorder(BorderFactory.createTitledBorder("Chart/Graph to File"));
        svgSavePanel.setLayout(new GridBagLayout());
        Caret caret = new Caret(0,0, 2);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;
        constraints.insets = new Insets(0, 10, 0,0);

        constraints = caret.asGridBag(constraints);
        svgSavePanel.add(svgFileNameLabel, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        svgFileNameTextField.setColumns(15);
        svgFileNameTextField.setToolTipText("The file will be saved in chosen result directory");
        svgSavePanel.add(svgFileNameTextField, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        svgSavePanel.add(widthLabel, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        svgWidthTextField.setColumns(15);
        svgSavePanel.add(svgWidthTextField, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        svgSavePanel.add(heightLabel, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        svgHeightTextField.setColumns(15);
        svgSavePanel.add(svgHeightTextField, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);
        JLabel saveOnOpen = new JLabel("Save SVG on Char/Graph Open");
        svgSavePanel.add(saveOnOpen, constraints);

        caret.next();
        constraints = caret.asGridBag(constraints);

        saveToSVGCheckBox.addItemListener(this::onSaveToSVGCheckBoxChanged);
        constraints.anchor = GridBagConstraints.LINE_START;
        svgSavePanel.add(saveToSVGCheckBox, constraints);


        return svgSavePanel;
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

    private void onScatterPlotCheckBoxChanged(ItemEvent event) {
        scatterPlot = event.getStateChange() == ItemEvent.SELECTED;
    }

    private void onGroupsInNotchedBoxesChanged(ItemEvent event){
        groupsInNotchedBoxes = event.getStateChange() == ItemEvent.SELECTED;
    }

    private void onLegendInNotchedBoxes(ItemEvent event) {
        legendInNotchedBoxes = event.getStateChange() == ItemEvent.SELECTED;
    }

    private void onSaveToSVGCheckBoxChanged(ItemEvent event) {
        saveToSVG = event.getStateChange() == ItemEvent.SELECTED;
        svgRelatedComponents.forEach(c -> c.setEnabled(saveToSVG));
    }

    private void onTableButtonClicked(ActionEvent event) {
        if (!hasSelection()) {
            return;
        }
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
        if (!hasSelection()) {
            return;
        }
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


        if (saveToSVG) {
            exportChartAsSVG(barChart);
        }
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        jDialog.add(chartPanel);
        jDialog.pack();
        jDialog.setLocationRelativeTo(Starter.getTopLevelFrame());
        jDialog.setVisible(true);
    }

    private void onNotchedBoxButtonClicked(ActionEvent event) {
        if (!hasSelection()) {
            return;
        }
        Map<String, List<Double>> flattenedData = analyzer.asFlattenedData(category, selectedMetrics);
        Map<String, List<Double>> notchedBoxFlattenedData;
        Map<String, String> metricGroups = new HashMap<>();
        if (groupsInNotchedBoxes) {
            List<HSDTestLetterRepresentationRow> result = computedHSDCache.computeIfAbsent(selectedMetricsToCacheKey(), k -> {
                RLanguageHSDTestRunner runner = new RLanguageHSDTestRunner(flattenedData);
                return runner.process();
            });
            notchedBoxFlattenedData = new LinkedHashMap<>();
            for (HSDTestLetterRepresentationRow hsdTestLetterRepresentationRow : result) {
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
        if (saveToSVG) {
            innerDialogPanel.exportChartAsSVG(
                    resultDirectory,
                    svgFileNameTextField.getText(),
                    svgWidthTextField.getText(),
                    svgHeightTextField.getText()
            );
        }
        JDialog jDialog = new JDialog(Starter.getTopLevelFrame(), "Notched Boxes View");
        jDialog.add(innerDialogPanel);
        jDialog.pack();
        jDialog.setLocationRelativeTo(Starter.getTopLevelFrame());
        jDialog.setVisible(true);
    }

    private boolean hasSelection() {
        boolean hasSelection = false;
        drop:
        for (Map.Entry<String, Map<String, Boolean>> metricSelection : selectedMetrics.entrySet()) {
            Map<String, Boolean> selectedSubMetrics = metricSelection.getValue();
            if (selectedSubMetrics == null) {
                continue;
            }
            for (Map.Entry<String, Boolean> selectedSubMetric : selectedSubMetrics.entrySet()) {
                Boolean selected = selectedSubMetric.getValue();
                if (selected != null && selected) {
                    hasSelection = true;
                    break drop;
                }
            }
        }
        if (!hasSelection) {
            JOptionPane.showMessageDialog(null, "Please select at least one metric");
        }
        return hasSelection;
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

    private void exportChartAsSVG(JFreeChart chart) {
        try {
            String fileName = svgFileNameTextField.getText().trim();
            if (!fileName.toLowerCase().endsWith(".svg")) {
                fileName += ".svg";
            }
            File svgFile = Paths.get(resultDirectory, fileName).toFile();
            GraphicsToSVGExporter exporter = new GraphicsToSVGExporter(svgFile);
            int width = Integer.parseInt(svgWidthTextField.getText().trim());
            int height = Integer.parseInt(svgHeightTextField.getText().trim());
            Rectangle bounds = new Rectangle(0, 0, width, height);
            exporter.export(g2 -> {
                chart.draw(g2, bounds);
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot save chart to SVG: " + e.getMessage());
        }
    }

}
