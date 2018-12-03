package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.starter.gui.main.event.model_event.MetricPanelModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.model.AutoSummENGSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.ModelsManager;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import java.awt.*;

public class AutoSummENGSelectionPanel extends JPanel {

    private final PubSub pubSub;

    private final AutoSummENGSelectionPanelModel model;

    private final JSpinner wordMinSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner wordMaxSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner wordDistSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner charMinSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner charMaxSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JSpinner charDistSpinnerchar = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

    private final JCheckBox wordEnabledCheckBox;


    private final JLabel wordOptions = new JLabel();

    private final JLabel characterOptions = new JLabel();



    public AutoSummENGSelectionPanel(PubSub pubSub, ModelsManager modelsManager) {
        this.pubSub = pubSub;
        this.model = new AutoSummENGSelectionPanelModel(pubSub);
        modelsManager.register(model);
        pubSub.subscribe(MetricPanelModelChangedEvent.class, this::onMetricModelPanelChanged);
        setLayout(new GridBagLayout());

        Insets leftLabelInsets = new Insets(0,20, 10, 20);
        int y = 0;
        int x = 0;
        GridBagConstraints metricLabelConstraints = new GridBagConstraints();
        metricLabelConstraints.gridx = x++;
        metricLabelConstraints.gridy = y;
        metricLabelConstraints.insets = leftLabelInsets;
        Font metricFont = new Font("Verdana", Font.BOLD + Font.ITALIC, 14);
        wordOptions.setFont(metricFont);
        wordOptions.setText("Word Options");
        add(wordOptions, metricLabelConstraints);

        //        System.out.println(y);
        GridBagConstraints rightSpringConstraints = new GridBagConstraints();
        rightSpringConstraints.gridx = x++;
        rightSpringConstraints.gridy = y++;
        rightSpringConstraints.weightx = 1;
        // rightSpringConstraints.gridwidth = 10;
        rightSpringConstraints.fill = GridBagConstraints.HORIZONTAL;
//        add(new JButton("Test"), rightSpringConstraints);
        add(new JPanel(), rightSpringConstraints);

        x = 0;
        JPanel wordInnerLinePanel = new JPanel();
        wordInnerLinePanel.setLayout(new GridBagLayout());
        GridBagConstraints wordInnerLinePanelConstraints = new GridBagConstraints();
        wordInnerLinePanelConstraints.gridx = x++;
        wordInnerLinePanelConstraints.gridy = y;
//        wordInnerLinePanelConstraints.weightx = 0;
        wordInnerLinePanelConstraints.gridwidth = 2;
        wordInnerLinePanelConstraints.anchor = GridBagConstraints.LINE_START;
        wordInnerLinePanelConstraints.insets = new Insets(0,40, 10, 20);
//        wordInnerLinePanelConstraints.fill = GridBagConstraints.HORIZONTAL;

        int innerX = 0, innerY = 0;
        JLabel wordEnabled = new JLabel("Enabled:");
        GridBagConstraints wordEnabledConstraints = new GridBagConstraints();
        wordEnabledConstraints.gridx = innerX++;
        wordEnabledConstraints.gridy = innerY;
        wordEnabledConstraints.insets = new Insets(0, 0, 10, 10);
        wordEnabledConstraints.anchor = GridBagConstraints.LINE_END;
        wordInnerLinePanel.add(wordEnabled, wordEnabledConstraints);

        wordEnabledCheckBox = new JCheckBox();
        GridBagConstraints wordEnabledCheckBoxConstraints = new GridBagConstraints();
        wordEnabledCheckBoxConstraints.gridx = innerX++;
        wordEnabledCheckBoxConstraints.gridy = innerY++;
        wordEnabledCheckBoxConstraints.anchor = GridBagConstraints.LINE_START;
        wordEnabledCheckBoxConstraints.insets = new Insets(0, 0, 10, 10);
        wordEnabledCheckBox.setBorder(BorderFactory.createMatteBorder(0,0,0,0, Color.BLACK));
        wordInnerLinePanel.add(wordEnabledCheckBox, wordEnabledCheckBoxConstraints);

        innerX = 0;
        JLabel wordMin = new JLabel("Word Min:");
        GridBagConstraints wordMinConstraints = new GridBagConstraints();
        wordMinConstraints.gridx = innerX++;
        wordMinConstraints.gridy = innerY;
        wordMinConstraints.insets = new Insets(0, 0, 10, 10);
        wordInnerLinePanel.add(wordMin, wordMinConstraints);

        GridBagConstraints wordMinSpinnerConstraints = new GridBagConstraints();
        wordMinSpinnerConstraints.gridx = innerX++;
        wordMinSpinnerConstraints.gridy = innerY;
        // wordMinSpinnerConstraints.insets = new Insets(0, 0, 10, 10);
        // wordMinSpinner.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.red));
        wordMinSpinnerConstraints.weighty = 1;
        wordMinSpinnerConstraints.fill = GridBagConstraints.VERTICAL;
        wordMinSpinnerConstraints.insets = new Insets(0, 0, 10, 20);;

        ((JSpinner.DefaultEditor)wordMinSpinner.getEditor()).getTextField().setColumns(4);
        wordInnerLinePanel.add(wordMinSpinner, wordMinSpinnerConstraints);



//        JLabel wordMax = new JLabel("Word Max");

        // x = 0;
        // innerX = 0;
        JLabel wordMax = new JLabel("Word Max:");
        GridBagConstraints wordMaxConstraints = new GridBagConstraints();
        wordMaxConstraints.gridx = innerX++;
        wordMaxConstraints.gridy = innerY;
        wordMaxConstraints.insets = new Insets(0, 0, 10, 10);;

        // wordMinConstraints.insets = new Insets(0, 0, 10, 0);
        wordInnerLinePanel.add(wordMax, wordMaxConstraints);

        GridBagConstraints wordMaxSpinnerConstraints = new GridBagConstraints();
        wordMaxSpinnerConstraints.gridx = innerX++;
        wordMaxSpinnerConstraints.gridy = innerY;
        // wordMinSpinnerConstraints.insets = new Insets(0, 0, 10, 10);
        // wordMinSpinner.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.red));
        ((JSpinner.DefaultEditor)wordMaxSpinner.getEditor()).getTextField().setColumns(4);
        wordMaxSpinnerConstraints.weighty = 1;
        wordMaxSpinnerConstraints.fill = GridBagConstraints.VERTICAL;
        wordMaxSpinnerConstraints.insets = new Insets(0, 0, 10, 20);;

        wordInnerLinePanel.add(wordMaxSpinner, wordMaxSpinnerConstraints);

        // innerX = 0;
        JLabel wordDist = new JLabel("Word Dist:");
        GridBagConstraints wordDistConstraints = new GridBagConstraints();
        wordDistConstraints.gridx = innerX++;
        wordDistConstraints.gridy = innerY;
        wordDistConstraints.insets = new Insets(0, 0, 10, 10);
        // wordMinConstraints.insets = new Insets(0, 0, 10, 0);
        wordInnerLinePanel.add(wordDist, wordDistConstraints);

        GridBagConstraints wordDistSpinnerConstraints = new GridBagConstraints();
        wordDistSpinnerConstraints.gridx = innerX++;
        wordDistSpinnerConstraints.gridy = innerY;
        // wordMinSpinnerConstraints.insets = new Insets(0, 0, 10, 10);
        // wordMinSpinner.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.red));
        ((JSpinner.DefaultEditor)wordDistSpinner.getEditor()).getTextField().setColumns(4);
        wordDistSpinnerConstraints.weighty = 1;
        wordDistSpinnerConstraints.insets = new Insets(0, 0, 10, 20);

        wordDistSpinnerConstraints.fill = GridBagConstraints.VERTICAL;
        wordInnerLinePanel.add(wordDistSpinner, wordDistSpinnerConstraints);

        add(wordInnerLinePanel, wordInnerLinePanelConstraints);

        // x = 0;
        // System.out.println("x: " + x);
        /*rightSpringConstraints = new GridBagConstraints();
        rightSpringConstraints.gridx = x++;
        rightSpringConstraints.gridy = y++;
        rightSpringConstraints.weightx = 1;
        // rightSpringConstraints.gridwidth = 11;
        rightSpringConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(new JButton("ss"), rightSpringConstraints);*/

    }

    private void onMetricModelPanelChanged(MetricPanelModelChangedEvent e) {
        boolean autoSummENGEnabled = e.getMetricPanelModel().isAutoSummENGEnabled();
        wordOptions.setEnabled(autoSummENGEnabled);
        this.wordEnabledCheckBox.setEnabled(autoSummENGEnabled);
        wordMinSpinner.setEnabled(autoSummENGEnabled);
        wordMaxSpinner.setEnabled(autoSummENGEnabled);
        wordDistSpinner.setEnabled(autoSummENGEnabled);
    }
}
