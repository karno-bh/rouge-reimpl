package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.NotchedBoxGraph;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.core.statistics.r_lang_gateway.RLanguageHSDTestRunner;
import il.ac.sce.ir.metric.core.utils.converter.GraphicsToSVGExporter;
import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.Starter;
import il.ac.sce.ir.metric.starter.gui.main.element.SystemMetricSubMetricCheckBox;
import il.ac.sce.ir.metric.starter.gui.main.util.Caret;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

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

    private boolean scatterPlot;

    private boolean saveToSVG;

    private final Map<String, Map<String, Boolean>> selectedMetrics = new HashMap<>();


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
        JPanel scatterPlotPanel = createScatterPlotPanel();

        svgFileNameTextField = new JTextField();
        saveToSVGCheckBox = new JCheckBox();
        svgWidthTextField = new JTextField();
        svgHeightTextField = new JTextField();
        svgRelatedComponents = Arrays.asList(
                svgFileNameLabel, widthLabel, heightLabel,
                svgFileNameTextField, svgWidthTextField, svgHeightTextField
        );
        svgRelatedComponents.forEach(c -> c.setEnabled(false));
        JPanel svgSavePanel = createSVGSavePanel();

        GridBagConstraints buttonsPanelConstraints = wholeSpaceFiller.getFillingConstraints();
        buttonsPanelConstraints.gridy = y++;
        add(svgSavePanel, buttonsPanelConstraints);
        buttonsPanelConstraints.gridy = y++;
        add(scatterPlotPanel, buttonsPanelConstraints);
        buttonsPanelConstraints.gridy = y++;
        add(analyzeButtonsPanel, buttonsPanelConstraints);

    }

    private JPanel createScatterPlotPanel() {
        JPanel scatterPlotPanel = new JPanel();
//        scatterPlotPanel.setBorder(BorderFactory.createTitledBorder("Notched Box Configuration"));
        scatterPlotPanel.setLayout(new GridBagLayout());
        WholeSpaceFiller filler = new WholeSpaceFiller();
        scatterPlotPanel.add(new JPanel(), filler.getFillingConstraints());
        GridBagConstraints scatterPlotConstraints = new GridBagConstraints();
        Insets scatterPlotPanelInsets = new Insets(0,0,0,10);
        scatterPlotConstraints.gridx = 1;
        scatterPlotConstraints.insets = scatterPlotPanelInsets;
        scatterPlotPanel.add(new JLabel("Notched Box with Jittered Scatter Plot"), scatterPlotConstraints);
        jitteredScatterPlotWithinNotchedBox.addItemListener(this::onScatterPlotCheckBoxChanged);
        scatterPlotConstraints.gridx = 2;
        scatterPlotConstraints.insets.right = 0;
        scatterPlotPanel.add(jitteredScatterPlotWithinNotchedBox);
        return scatterPlotPanel;
    }

    private JPanel createSVGSavePanel() {
        JPanel svgSavePanel = new JPanel();
        svgSavePanel.setBorder(BorderFactory.createTitledBorder("Chart/Graph to File"));
        svgSavePanel.setLayout(new GridBagLayout());
        Caret caret = new Caret(0,0, 2);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.LINE_END;

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
        svgSavePanel.add(saveToSVGCheckBox, constraints);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new GridBagLayout());
        WholeSpaceFiller filler = new WholeSpaceFiller();
        GridBagConstraints fillingConstraints = filler.getFillingConstraints();
        wrapperPanel.add(new JPanel(), fillingConstraints);
        GridBagConstraints svgSavePanelConstraints = new GridBagConstraints();
        svgSavePanelConstraints.gridx = 1;
        wrapperPanel.add(svgSavePanel, svgSavePanelConstraints);
        return wrapperPanel;
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

    private void onSaveToSVGCheckBoxChanged(ItemEvent event) {
        saveToSVG = event.getStateChange() == ItemEvent.SELECTED;
        svgRelatedComponents.forEach(c -> c.setEnabled(saveToSVG));
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
        Map<String, List<Double>> flattenedData = analyzer.asFlattenedData(category, selectedMetrics);
        RLanguageHSDTestRunner runner = new RLanguageHSDTestRunner(flattenedData);
        List<Map<String, Object>> result = runner.process();
        System.out.println(result);

        AnalyzeDialogNotchedBoxPanel innerDialogPanel = new AnalyzeDialogNotchedBoxPanel(flattenedData, scatterPlot);
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
