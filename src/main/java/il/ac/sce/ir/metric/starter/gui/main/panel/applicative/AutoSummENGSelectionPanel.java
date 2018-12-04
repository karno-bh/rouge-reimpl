package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.concrete_metric.auto_summ_eng.data.SimpleTextConfig;
import il.ac.sce.ir.metric.starter.gui.main.event.component_event.AutoSummENGSelectionPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.MetricPanelModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.model.AutoSummENGSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.ModelsManager;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;

public class AutoSummENGSelectionPanel extends JPanel {

    private final PubSub pubSub;

    private final AutoSummENGSelectionPanelModel model;

    private final JSpinner wordMinSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner wordMaxSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner wordDistSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner charMinSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner charMaxSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner charDistSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JCheckBox wordEnabledCheckBox = new JCheckBox();

    private final JCheckBox charEnabledCheckBox = new JCheckBox();

    private final JLabel wordOptions = new JLabel();

    private final JLabel charOptions = new JLabel();


    public AutoSummENGSelectionPanel(PubSub pubSub, ModelsManager modelsManager) {
        this.pubSub = pubSub;
        this.model = new AutoSummENGSelectionPanelModel(pubSub);
        modelsManager.register(model);
        pubSub.subscribe(MetricPanelModelChangedEvent.class, this::onMetricModelPanelChanged);
        setLayout(new GridBagLayout());
        subscribeOnInnerEvents();

        int y = 0;

        constructHeader(wordOptions, "Word Options", y++);
        JPanel wordInnerLinePanel = new JPanel();
        wordInnerLinePanel.setLayout(new GridBagLayout());
        GridBagConstraints wordInnerLinePanelConstraints = getInnerSelectionPanelConstraints(0, y++);
        constructInnerWordPanel(wordInnerLinePanel);
        add(wordInnerLinePanel, wordInnerLinePanelConstraints);

        constructHeader(charOptions, "Character Options", y++);
        JPanel characterInnerLinePanel = new JPanel();
        characterInnerLinePanel.setLayout(new GridBagLayout());
        GridBagConstraints characterInnerLinePanelConstraints = getInnerSelectionPanelConstraints(0, y);
        constuctCharacterInnerLinePanel(characterInnerLinePanel);
        add(characterInnerLinePanel, characterInnerLinePanelConstraints);
    }

    private void subscribeOnInnerEvents() {

        wordEnabledCheckBox.addItemListener(this::onWordEnabledCheckBoxChanged);
        charEnabledCheckBox.addItemListener(this::onCharEnabledCheckBoxChanged);

        wordMinSpinner.addChangeListener(this::onWordSpinnerChanged);
        wordMaxSpinner.addChangeListener(this::onWordSpinnerChanged);
        wordDistSpinner.addChangeListener(this::onWordSpinnerChanged);

        charMinSpinner.addChangeListener(this::onCharSpinnerChanged);
        charMaxSpinner.addChangeListener(this::onCharSpinnerChanged);
        charDistSpinner.addChangeListener(this::onCharSpinnerChanged);
    }

    private void constructInnerWordPanel(JPanel wordInnerLinePanel) {
        constructCommonInnerPanel(wordInnerLinePanel,
                wordEnabledCheckBox,
                "Word Min:", wordMinSpinner,
                "Word Max:", wordMaxSpinner,
                "Word Dist:", wordDistSpinner);
    }

    private void constuctCharacterInnerLinePanel(JPanel characterInnerLinePanel) {
        constructCommonInnerPanel(characterInnerLinePanel,
                charEnabledCheckBox,
                "Char Min:", charMinSpinner,
                "Char Max:", charMaxSpinner,
                "Char Dist:", charDistSpinner);
    }

    private void constructCommonInnerPanel(JPanel panel,
                                           JCheckBox enableCheckBox,
                                           String minSpinnerText,
                                           JSpinner minSpinner,
                                           String maxSpinnerText,
                                           JSpinner maxSpinner,
                                           String distSpinnerText,
                                           JSpinner disSpinner) {
        int innerX = 0, innerY = 0;
        JLabel wordEnabled = new JLabel("Enabled:");
        panel.add(wordEnabled, getEnabledLabelConstraints(innerX++, innerY));

        enableCheckBox.setBorder(BorderFactory.createMatteBorder(0,0,0,0, Color.BLACK));
        panel.add(enableCheckBox, getEnableCheckBoxConstraints(innerX++, innerY++));

        Arrays.asList(minSpinner, maxSpinner, disSpinner).forEach(this::setSpinnerColumns);

        innerX = 0;
        panel.add(new JLabel(minSpinnerText), getSpinnerLabelConstraints(innerX++, innerY));
        panel.add(minSpinner, getSpinnerConstraints(innerX++, innerY));

        panel.add(new JLabel(maxSpinnerText), getSpinnerLabelConstraints(innerX++, innerY));
        panel.add(maxSpinner, getSpinnerConstraints(innerX++, innerY));

        panel.add(new JLabel(distSpinnerText), getEnabledLabelConstraints(innerX++, innerY));
        panel.add(disSpinner, getSpinnerConstraints(innerX++, innerY));
    }

    private GridBagConstraints getInnerSelectionPanelConstraints(int x, int y) {
        GridBagConstraints innerSelectionPanelConstraints = new GridBagConstraints();
        innerSelectionPanelConstraints.gridx = x;
        innerSelectionPanelConstraints.gridy = y;
        innerSelectionPanelConstraints.gridwidth = 2;
        innerSelectionPanelConstraints.anchor = GridBagConstraints.LINE_START;
        innerSelectionPanelConstraints.insets = new Insets(0,40, 10, 20);
        return innerSelectionPanelConstraints;
    }

    private GridBagConstraints getEnabledLabelConstraints(int x, int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.insets = new Insets(0, 0, 10, 10);
        constraints.anchor = GridBagConstraints.LINE_END;
        return constraints;
    }

    private GridBagConstraints getEnableCheckBoxConstraints(int x, int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.insets = new Insets(0, 0, 10, 10);
        return constraints;
    }

    private GridBagConstraints getSpinnerLabelConstraints(int x, int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.insets = new Insets(0, 0, 10, 10);
        return constraints;
    }

    private GridBagConstraints getSpinnerConstraints(int x, int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.insets = new Insets(0, 0, 10, 20);
        return constraints;
    }

    private void setSpinnerColumns(JSpinner spinner) {
        ((JSpinner.DefaultEditor)spinner.getEditor()).getTextField().setColumns(4);
    }

    private void constructHeader(JLabel headerLabel, String headerLabelText, int y) {
        int x = 0;
        Insets leftLabelInsets = new Insets(0,20, 10, 20);
        GridBagConstraints metricLabelConstraints = new GridBagConstraints();
        metricLabelConstraints.gridx = x++;
        metricLabelConstraints.gridy = y;
        metricLabelConstraints.insets = leftLabelInsets;
        metricLabelConstraints.anchor = GridBagConstraints.LINE_START;
        Font metricFont = new Font("Verdana", Font.BOLD + Font.ITALIC, 14);
        headerLabel.setFont(metricFont);
        headerLabel.setText(headerLabelText);
        add(headerLabel, metricLabelConstraints);

        //        System.out.println(y);
        GridBagConstraints rightSpringConstraints = new GridBagConstraints();
        rightSpringConstraints.gridx = x++;
        rightSpringConstraints.gridy = y;
        rightSpringConstraints.weightx = 1;
        rightSpringConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(new JPanel(), rightSpringConstraints);
    }

    private void onMetricModelPanelChanged(MetricPanelModelChangedEvent e) {
        boolean autoSummENGEnabled = e.getMetricPanelModel().isAutoSummENGEnabled();
        wordOptions.setEnabled(autoSummENGEnabled);
        wordEnabledCheckBox.setEnabled(autoSummENGEnabled);
        wordMinSpinner.setEnabled(autoSummENGEnabled);
        wordMaxSpinner.setEnabled(autoSummENGEnabled);
        wordDistSpinner.setEnabled(autoSummENGEnabled);

        charOptions.setEnabled(autoSummENGEnabled);
        charEnabledCheckBox.setEnabled(autoSummENGEnabled);
        charMinSpinner.setEnabled(autoSummENGEnabled);
        charMaxSpinner.setEnabled(autoSummENGEnabled);
        charDistSpinner.setEnabled(autoSummENGEnabled);
    }

    private void onWordEnabledCheckBoxChanged(ItemEvent itemEvent) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;

        AutoSummENGSelectionPanelEvent event = new AutoSummENGSelectionPanelEvent(
                AutoSummENGSelectionPanelEvent.SelectionType.WORD_N_GRAMS_SELECTED, selected);
        pubSub.publish(event);
    }

    private void onCharEnabledCheckBoxChanged(ItemEvent itemEvent) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;

        AutoSummENGSelectionPanelEvent event = new AutoSummENGSelectionPanelEvent(
                AutoSummENGSelectionPanelEvent.SelectionType.CHARACTER_N_GRAMS_SELECTED, selected);
        pubSub.publish(event);
    }

    private void onWordSpinnerChanged(ChangeEvent changeEvent) {
        int min = (int)wordMinSpinner.getValue();
        int max = (int)wordMaxSpinner.getValue();
        int dist = (int)wordDistSpinner.getValue();

        AutoSummENGSelectionPanelEvent event = new AutoSummENGSelectionPanelEvent(
                AutoSummENGSelectionPanelEvent.SelectionType.WORD_N_GRAMS_VALUES_CHANGED, min, max, dist);

        pubSub.publish(event);
    }

    private void onCharSpinnerChanged(ChangeEvent changeEvent) {
        int min = (int)charMinSpinner.getValue();
        int max = (int)charMaxSpinner.getValue();
        int dist = (int)charDistSpinner.getValue();

        AutoSummENGSelectionPanelEvent event = new AutoSummENGSelectionPanelEvent(
                AutoSummENGSelectionPanelEvent.SelectionType.CHARACTER_N_GRAMS_VALUES_CHANGED, min, max, dist);

        pubSub.publish(event);
    }
}
