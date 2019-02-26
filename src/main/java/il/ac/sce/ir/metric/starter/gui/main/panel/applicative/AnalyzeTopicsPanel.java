package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.gui.data.ColoredCell;
import il.ac.sce.ir.metric.core.gui.data.Table;
import il.ac.sce.ir.metric.core.utils.TableTabulator;
import il.ac.sce.ir.metric.core.utils.converter.GraphicsToSVGExporter;
import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.Starter;
import il.ac.sce.ir.metric.starter.gui.main.element.SystemMetricSubMetricCheckBox;
import il.ac.sce.ir.metric.starter.gui.main.panel.applicative.utils.AnalyzePanelsCommons;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.SVGSavePanel;
import il.ac.sce.ir.metric.starter.gui.main.util.Caret;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class AnalyzeTopicsPanel extends JPanel {

    private final String category;

    private final String resultDirectory;

    private final ResultsMetricHierarchyAnalyzer analyzer;

    private final Map<String, Boolean> selectedSystems = new HashMap<>();

    private final Map<String, Map<String, Boolean>> selectedMetric = new HashMap<>();

    private final JButton table;

    private final JButton barChart;

    private final JButton metricHeat;

    private final JButton averageMetricHeat;

    private final SVGSavePanel svgSavePanel = new SVGSavePanel();

    private final AnalyzePanelsCommons analyzePanelsCommons = new AnalyzePanelsCommons();

    public AnalyzeTopicsPanel(String category, ResultsMetricHierarchyAnalyzer analyzer, String resultDirectory) {
        this.category = category;
        this.analyzer = analyzer;
        this.resultDirectory = resultDirectory;
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


        constraints.gridy = y++;
        JPanel svgSavePanelWrapper = new JPanel();
        svgSavePanelWrapper.setLayout(new GridBagLayout());
        WholeSpaceFiller svgFiller = new WholeSpaceFiller();
        GridBagConstraints fillingConstraints = svgFiller.getFillingConstraints();
        svgSavePanelWrapper.add(new JPanel(), fillingConstraints);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        svgSavePanelWrapper.add(svgSavePanel, c);
        add(svgSavePanelWrapper, constraints);

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
        metricHeat = new JButton("Metric Heat");
        metricHeat.addActionListener(this::onMetricHeatClicked);
        buttonsPanel.add(metricHeat, buttonsConstraints);

        buttonsConstraints.gridx = buttonsX++;
        averageMetricHeat = new JButton("All Metrics Avg Heat");
        averageMetricHeat.addActionListener(this::onAverageMetricHeatClicked);
        buttonsPanel.add(averageMetricHeat, buttonsConstraints);

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
        if (!analyzePanelsCommons.hasSelection(selectedMetric, this)) {
            return;
        }
        Table tableData = analyzer.asTopicTable(category, selectedSystems, selectedMetric);
        analyzePanelsCommons.filterTableDisplaying(tableData);
        JDialog jDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Table View");
        JTable systemsSummary = new JTable(tableData);
        systemsSummary.setFillsViewportHeight(true);
        systemsSummary.setAutoCreateRowSorter(true);
        systemsSummary.setPreferredScrollableViewportSize(new Dimension(800, 600));
        jDialog.add(new JScrollPane(systemsSummary));
        jDialog.pack();
        jDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        jDialog.setVisible(true);
    }

    private void onMetricHeatClicked(ActionEvent event) {
        Map<String, Set<String>> availableSystems = analyzer.getAvailableSystems();
        Set<String> categoryAvailableSystem = new TreeSet<>(availableSystems.get(category));
        Map<String, Boolean> selectedSystems = new TreeMap<>();
        categoryAvailableSystem.forEach(system -> {
            if (!Constants.VIRTUAL_TOPIC_SYSTEM.equals(system)) {
                selectedSystems.put(system, true);
            }
        });
        Map<String, Boolean> categoryMetrics = selectedMetric.get(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE);
        int selections = 0;
        for (Map.Entry<String, Boolean> metricsSelection : categoryMetrics.entrySet()) {
            Boolean value = metricsSelection.getValue();
            if (value != null && value) {
                selections++;
            }
            if (selections > 1) {
                JOptionPane.showMessageDialog(this, "Heat table supports 1 metric maximum");
                return;
            }
        }
        if (selections == 0) {
            JOptionPane.showMessageDialog(this, "Please select a metric");
            return;
        }
        Table tableData = analyzer.asTopicTable(category, selectedSystems, selectedMetric);
        analyzer.addAverageToTableAndSort(tableData);
        int topicColumn = analyzer.getTopicColumn(tableData);
        LinkedHashMap<String, List<ColoredCell>> heatMap = analyzer.tableToHeatMap(tableData, topicColumn);

        DecimalFormat df = new DecimalFormat("#.####");
        List<List<String>> heatMapAsStrVals = new ArrayList<>();
        heatMapAsStrVals.add(new ArrayList<>());
        List<String> tableDataColumns = tableData.getColumns();
        int rowNum = 0;
        for (Map.Entry<String, List<ColoredCell>> topicToValuesEntry : heatMap.entrySet()) {
            String topic = topicToValuesEntry.getKey();
            List<ColoredCell> values = topicToValuesEntry.getValue();

            List<String> stringValues = new ArrayList<>();
            if (rowNum == 0) {
                List<String> header = heatMapAsStrVals.get(0);
                header.add(Constants.TOPIC);
            }
            if (Constants.AVERAGE_VIRTUAL_ROW.equals(topic)) {
                topic = "Avg";
            }
            stringValues.add(topic);
            for (ColoredCell coloredCell : values) {
                int tableIndex = coloredCell.getTableIndex();
                if (rowNum == 0) {
                    String systemName = tableDataColumns.get(tableIndex);
                    systemName = systemName.substring(0, systemName.indexOf("/")).trim();
                    heatMapAsStrVals.get(0).add(systemName);
                }
                stringValues.add("" + df.format(coloredCell.getValue()));
            }
            heatMapAsStrVals.add(stringValues);

            rowNum++;
        }

        TableTabulator tabulator = new TableTabulator(heatMapAsStrVals);
        tabulator.tableAsTabularString();

        JDialog jDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Readability Heat Diff");
        JTextArea textArea = new JTextArea(48, 160);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        String tableName = "Diff Heat Table:\n\n";

        List<List<String>> stringRepresentation = analyzer.tableToStringRepresentation(
                tableData,
                s -> s.indexOf("/") > 0 ? s.substring(0, s.indexOf("/")).trim() : s,
                s -> Constants.AVERAGE_VIRTUAL_ROW.equals(s) ? "Avg" : s);
        TableTabulator origTable = new TableTabulator(stringRepresentation);
        origTable.tableAsTabularString();

        textArea.setText(tableName + tabulator.getResultTable() + "\n\nOriginal Values:\n\n" + origTable.getResultTable());

        List<int[]> lineRanges = tabulator.getLineRanges();
        int lineSize = tabulator.getResultLines().get(0).length() + 1;

        Highlighter highlighter = textArea.getHighlighter();
        try {
            rowNum = 1;
            for (Map.Entry<String, List<ColoredCell>> topicToValuesEntry : heatMap.entrySet()) {
                int columnNum = 1;
                for (ColoredCell coloredCell : topicToValuesEntry.getValue()) {
                    int[] ranges = lineRanges.get(columnNum);
                    int leftOffset = rowNum * lineSize + ranges[0] + tableName.length();
                    int rightOffset = rowNum * lineSize + ranges[1] + tableName.length();
                    Highlighter.HighlightPainter painter =
                            new DefaultHighlighter.DefaultHighlightPainter(coloredCell.getColor());
                    highlighter.addHighlight(leftOffset, rightOffset, painter);

                    columnNum++;
                }

                rowNum++;
            }

        } catch (BadLocationException e) {
            throw new RuntimeException("Error while calculating highlighters", e);
        }



        jDialog.add(new JScrollPane(textArea));
        jDialog.pack();
        jDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        jDialog.setVisible(true);
    }

    private void onAverageMetricHeatClicked(ActionEvent event) {
        Map<String, Set<String>> availableSystems = analyzer.getAvailableSystems();
        Set<String> categoryAvailableSystem = new TreeSet<>(availableSystems.get(category));
        Map<String, Boolean> selectedSystems = new TreeMap<>();
        categoryAvailableSystem.forEach(system -> {
            if (!Constants.VIRTUAL_TOPIC_SYSTEM.equals(system)) {
                selectedSystems.put(system, true);
            }
        });
        Map<String, Boolean> selectedMetrics = selectedMetric.get(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE);
        Map<String, LinkedHashMap<String, List<ColoredCell>>> metricHeats = new LinkedHashMap<>();
        Map<String, Table> originalMetricTables = new LinkedHashMap<>();
        for (String metric : new TreeSet<>(selectedMetrics.keySet())) {
            Map<String, Map<String, Boolean>> dummySelectedMetric = new HashMap<>();
            Map<String, Boolean> innerDummySelectedMetric =
                    dummySelectedMetric.computeIfAbsent(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE, k -> new HashMap<>());
            innerDummySelectedMetric.put(metric, true);

            Table tableData = analyzer.asTopicTable(category, selectedSystems, dummySelectedMetric);
            analyzer.addAverageToTableAndSort(tableData);
            int topicColumn = analyzer.getTopicColumn(tableData);
            LinkedHashMap<String, List<ColoredCell>> heatMap = analyzer.tableToHeatMap(tableData, topicColumn);

            metricHeats.put(metric, heatMap);
            originalMetricTables.put(metric, tableData);
        }

        DecimalFormat df = new DecimalFormat("#.####");
        List<List<String>> avgStrTable = new ArrayList<>();
        List<List<String>> originalStrTable = new ArrayList<>();
        avgStrTable.add(new ArrayList<>());
        originalStrTable.add(new ArrayList<>());
        int rowNum = 0;
        for (Map.Entry<String, LinkedHashMap<String, List<ColoredCell>>> metricToHeatMap : metricHeats.entrySet()) {
            String metric = metricToHeatMap.getKey();
            if (rowNum == 0) {
                avgStrTable.get(0).add("Metric");
                originalStrTable.get(0).add("Metric");
            }
            List<String> values = new ArrayList<>();
            List<String> origStrTableValues = new ArrayList<>();
            values.add(metric);
            origStrTableValues.add(metric);
            LinkedHashMap<String, List<ColoredCell>> heatMap = metricToHeatMap.getValue();
            List<ColoredCell> coloredCells = heatMap.get(Constants.AVERAGE_VIRTUAL_ROW);
            Table originalTable = originalMetricTables.get(metric);
            int columnNum;
            if (rowNum == 0) {
                columnNum = 0;
                for (String column : originalTable.getColumns()) {
                    if (columnNum != 0)  {
                        String columnName = filterSystem(column);
                        columnName = columnName
                                .replace(Constants.ELENA_READABILITY_LOWER_CASE, Constants.READABILITY_LOWER_CASE)
                                .replace(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE, Constants.TOPICS_READABILITY_LOWER_CASE);
                        originalStrTable.get(0).add(columnName);
                    }
                    columnNum++;
                }
            }
            List<Object> origTableAvgRow = originalTable.getData().get(originalTable.getData().size() - 1);
            columnNum = 0;
            for (Object column : origTableAvgRow) {
                if (columnNum != 0) {
                    origStrTableValues.add(df.format((double) column));
                }

                columnNum++;
            }
            for (ColoredCell coloredCell : coloredCells) {
                if (rowNum == 0) {
                    String system = originalTable.getColumns().get(coloredCell.getTableIndex());
                    system = filterSystem(system);
                    avgStrTable.get(0).add(system);
                }
                values.add(df.format(coloredCell.getValue()));
            }
            avgStrTable.add(values);
            originalStrTable.add(origStrTableValues);
            rowNum++;
        }


        TableTabulator tabulator = new TableTabulator(avgStrTable);
        tabulator.tableAsTabularString();
        // System.out.println(tabulator.getResultTable());

        JDialog jDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Readability Avg Heat Diff");
        JTextArea textArea = new JTextArea(48, 160);
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        String tableName = "All Diff Heat Table:\n\n";

        TableTabulator origTableTabulator = new TableTabulator(originalStrTable);
        origTableTabulator.tableAsTabularString();

        textArea.setText(tableName + tabulator.getResultTable() + "\n\nOriginal Values:\n\n" + origTableTabulator.getResultTable());

        List<int[]> lineRanges = tabulator.getLineRanges();
        int lineSize = tabulator.getResultLines().get(0).length() + 1;
        Highlighter highlighter = textArea.getHighlighter();
        try {
            rowNum = 1;
            for (Map.Entry<String, LinkedHashMap<String, List<ColoredCell>>> metricToHeatMap : metricHeats.entrySet()) {
                int columnNum = 1;
                LinkedHashMap<String, List<ColoredCell>> heatMap = metricToHeatMap.getValue();
                List<ColoredCell> coloredCells = heatMap.get(Constants.AVERAGE_VIRTUAL_ROW);
                for (ColoredCell coloredCell : coloredCells) {
                    int[] ranges = lineRanges.get(columnNum);
                    int leftOffset = rowNum * lineSize + ranges[0] + tableName.length();
                    int rightOffset = rowNum * lineSize + ranges[1] + tableName.length();
                    Highlighter.HighlightPainter painter =
                            new DefaultHighlighter.DefaultHighlightPainter(coloredCell.getColor());
                    highlighter.addHighlight(leftOffset, rightOffset, painter);

                    columnNum++;
                }

                rowNum++;
            }

        } catch (BadLocationException e) {
            throw new RuntimeException("Error while calculating highlighters", e);
        }

        jDialog.add(new JScrollPane(textArea));
        jDialog.pack();
        jDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        jDialog.setVisible(true);
    }




    private void onBarChartClicked(ActionEvent event) {
        if (!analyzePanelsCommons.hasSelection(selectedMetric, this)) {
            return;
        }
        JDialog jDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Bar Chart View");
        Table tableData = analyzer.asTopicTable(category, selectedSystems, selectedMetric);
        analyzePanelsCommons.filterTableDisplaying(tableData);
        CategoryDataset categoryDataset = analyzer.tableToCategoryDataSet(tableData);
        JFreeChart barChart = ChartFactory.createBarChart(
                "Topic Average vs Summary Value",
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

    private List<JPanel> getMetricPanels(Map<String, List<String>> topicSubMetrics) {
        List<JPanel> metricPanels = new ArrayList<>();
        topicSubMetrics.forEach((metricKey, subMetrics) -> {
            JPanel metricPanel = new JPanel();
            metricPanel.setLayout(new GridBagLayout());
            metricPanel.setBorder(BorderFactory.createTitledBorder(
                    metricKey
                            .replace(Constants.ELENA_READABILITY_LOWER_CASE, Constants.READABILITY_LOWER_CASE)
                            .replace(Constants.ELENA_TOPICS_READABILITY_LOWER_CASE, Constants.TOPICS_READABILITY_LOWER_CASE)
            ));
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

    private String filterSystem(String s) {
        return s.indexOf("/") > 0 ? s.substring(0, s.indexOf("/")).trim() : s;
    }
}
