package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.core.config.Constants;
import il.ac.sce.ir.metric.core.utils.result.ResultsMetricHierarchyAnalyzer;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AnalyzeBySystemPanel extends JPanel {

    private final String category;

    private final ResultsMetricHierarchyAnalyzer analyzer;

    private final JButton tableButton;

    private final JButton barChartButton;

    private final JButton notchedBoxButton;

    public AnalyzeBySystemPanel(String category, ResultsMetricHierarchyAnalyzer analyzer) {
        this.category = category;
        this.analyzer = analyzer;
        TitledBorder titledBorder = BorderFactory.createTitledBorder("System Level");
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
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridBagLayout());
        WholeSpaceFiller wholeSpaceFiller = new WholeSpaceFiller();
        GridBagConstraints buttonsPanelSpringConstraints = wholeSpaceFiller.getFillingConstraints();
        JPanel buttonsPanelSpring = new JPanel();
        buttonsPanel.add(buttonsPanelSpring, buttonsPanelSpringConstraints);
        int buttonsPanelX = 1;

        Insets buttonInsets = new Insets(20,0,0,10);

        GridBagConstraints tableButtonConstraints = new GridBagConstraints();
        tableButtonConstraints.gridx = buttonsPanelX++;
        tableButtonConstraints.insets = buttonInsets;

        tableButton = new JButton("Table");
        buttonsPanel.add(tableButton, tableButtonConstraints);

        barChartButton = new JButton("Bar Chart");
        tableButtonConstraints.gridx = buttonsPanelX++;
        buttonsPanel.add(barChartButton, tableButtonConstraints);

        notchedBoxButton = new JButton("Notched Boxes");
        // tableButtonConstraints = new GridBagConstraints();
        tableButtonConstraints.gridx = buttonsPanelX++;
        tableButtonConstraints.insets.right = 0;
        buttonsPanel.add(notchedBoxButton, tableButtonConstraints);

        GridBagConstraints buttonsPanelConstraints = wholeSpaceFiller.getFillingConstraints();
        buttonsPanelConstraints.gridy = y++;
        add(buttonsPanel, buttonsPanelConstraints);
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
            JPanel panel = new JPanel();
            panel.setBorder(titledBorder);
            Set<String> availableSubMetrics = analyzer.getAvailableSubMetric(category, nonVirtualSystem, availableSystemMetric);
            for (String availableSubMetric : availableSubMetrics) {
                panel.add(new JLabel(availableSubMetric));
            }
            metricPanels.add(panel);
        }


        return metricPanels;
    }



}
