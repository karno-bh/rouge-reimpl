package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.starter.gui.main.event.model_event.MetricPanelModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.component_event.RougeSelectionPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.model.RougeSelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.ModelsManager;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

public class RougeSelectionPanel extends JPanel {

    private final PubSub pubSub;

    private final RougeSelectionPanelModel model;

    private final java.util.List<JCheckBox> nGramsCheckBoxes;

    private final JTextField additionalnGrams;

    private final JCheckBox rougeSEnabledCheckbox;

    private final JCheckBox rougeSUseUnigramsCheckBox;

    private final JSpinner skipDistance = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));

    private final JCheckBox rougeLEnabledCheckbox;

    private final JCheckBox rougeWEnabledCheckbox;

    private final JLabel rougeN;

    private final JLabel rougeS;

    private final JLabel rougeL;

    private final JLabel rougeW;

    public RougeSelectionPanel(PubSub pubSub, ModelsManager modelsManager) {

        this.pubSub = pubSub;

        this.model = new RougeSelectionPanelModel(pubSub);
        modelsManager.register(model);

        pubSub.subscribe(MetricPanelModelChangedEvent.class, this::onMetricModelPanelChanged);
        // model.publishSelf();

        setLayout(new GridBagLayout());
        Insets leftLabelInsets = new Insets(0,20, 10, 20);
        int y = 0;
        int x = 0;
        GridBagConstraints metricLabelConstraints = new GridBagConstraints();
        metricLabelConstraints.gridx = x++;
        metricLabelConstraints.gridy = y;
        metricLabelConstraints.insets = leftLabelInsets;

        Font metricFont = new Font("Verdana", Font.BOLD + Font.ITALIC, 14);
        rougeN = new JLabel("RougeN");
        rougeN.setEnabled(false);
        rougeN.setFont(metricFont);
//        rougeN.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.red));
        add(rougeN, metricLabelConstraints);

//        System.out.println(y);
        GridBagConstraints rightSpringConstraints = new GridBagConstraints();
        rightSpringConstraints.gridx = x++;
        rightSpringConstraints.gridy = y++;
        rightSpringConstraints.weightx = 1;
        rightSpringConstraints.gridwidth = 20;
        rightSpringConstraints.fill = GridBagConstraints.HORIZONTAL;
//        add(new JButton("Test"), rightSpringConstraints);
        add(new JPanel(), rightSpringConstraints);

//        System.out.println(y);
        x = 0;
        JLabel nGrams = new JLabel("N Grams:");
        // nGrams.setFont(metricFont);
        GridBagConstraints nGramLabelConstraints = new GridBagConstraints();
        nGramLabelConstraints.gridx = x++;
        nGramLabelConstraints.gridy = y;
        nGramLabelConstraints.insets = new Insets(0, 20, 10, 10);
        add(nGrams, nGramLabelConstraints);

        nGramsCheckBoxes = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            JLabel nGramLabel = new JLabel(i + ":");
            GridBagConstraints nGramConstraints = new GridBagConstraints();
            nGramConstraints.gridx = x++;
            nGramConstraints.gridy = y;
            nGramConstraints.insets = new Insets(0, 0, 10, 5);
            add(nGramLabel, nGramConstraints);

            JCheckBox nGramCheckBox = new JCheckBox();
            nGramCheckBox.setEnabled(false);

            GridBagConstraints nGramCheckBoxConstraints = new GridBagConstraints();
            nGramsCheckBoxes.add(nGramCheckBox);
            nGramCheckBoxConstraints.gridx = x++;
            nGramCheckBoxConstraints.gridy = y;
            nGramCheckBoxConstraints.insets = new Insets(0,0,10, 5);
            add(nGramCheckBox, nGramCheckBoxConstraints);
        }

        nGramsCheckBoxes.get(0).addItemListener(this::onRouge1CheckBoxChanged);
        nGramsCheckBoxes.get(1).addItemListener(this::onRouge2CheckBoxChanged);
        nGramsCheckBoxes.get(2).addItemListener(this::onRouge3CheckBoxChanged);

        GridBagConstraints middleSpringConstraints = new GridBagConstraints();
        middleSpringConstraints.gridx = x++;
        middleSpringConstraints.gridy = y;
        middleSpringConstraints.weightx = 1;
        middleSpringConstraints.fill = GridBagConstraints.HORIZONTAL;
//        add(new JButton("Test"), middleSpringConstraints);
        add(new JPanel(), middleSpringConstraints);

        JLabel more = new JLabel("More (by comma): ");
        GridBagConstraints moreConstraints = new GridBagConstraints();
        moreConstraints.gridx = x++;
        moreConstraints.gridy = y;
        moreConstraints.insets = new Insets(0, 0, 10, 5);
        add(more, moreConstraints);

        JTextField moreText = new JTextField();
        moreText.setEnabled(false);
        moreText.setColumns(5);
        this.additionalnGrams = moreText;
        this.additionalnGrams.addFocusListener(new AdditionalnGramsFocusListener());
        this.additionalnGrams.addActionListener(this::onAdditionalnGramsSubmit);
        GridBagConstraints moreTextConstraints = new GridBagConstraints();
        moreTextConstraints.gridx = x++;
        moreTextConstraints.gridy = y++;
        moreTextConstraints.weighty = 1;
        moreTextConstraints.fill = GridBagConstraints.VERTICAL;
        moreTextConstraints.insets = new Insets(0,0,10, 0);
        add(moreText, moreTextConstraints);

        x = 0;
        rougeS = new JLabel("RougeS");
        rougeS.setEnabled(false);
        rougeS.setFont(metricFont);

        metricLabelConstraints.gridx = x++;
        metricLabelConstraints.gridy = y;
        add(rougeS, metricLabelConstraints);

        rightSpringConstraints.gridx = x++;
        rightSpringConstraints.gridy = y++;
        add(new JPanel(), rightSpringConstraints);

        x = 0;
        JLabel rougeSEnabledLabel = new JLabel("Enabled:");
        GridBagConstraints rougeSEnabledConstraints = new GridBagConstraints();
        rougeSEnabledConstraints.gridx = x++;
        rougeSEnabledConstraints.gridy = y;
        rougeSEnabledConstraints.insets = new Insets(0, 20, 10, 10);
        add(rougeSEnabledLabel, rougeSEnabledConstraints);

        rougeSEnabledCheckbox = new JCheckBox();
        rougeSEnabledCheckbox.setEnabled(false);
        rougeSEnabledCheckbox.addItemListener(this::onRougeSCheckBoxChanged);
        GridBagConstraints rougeSEnabledCheckboxConstraints = new GridBagConstraints();
        rougeSEnabledCheckboxConstraints.gridx = x++;
        rougeSEnabledCheckboxConstraints.gridy = y++;
        rougeSEnabledCheckboxConstraints.insets = new Insets(0,0,10, 5);
        add(rougeSEnabledCheckbox, rougeSEnabledCheckboxConstraints);

        x = 0;
        JLabel rougeSUseUnigramsLabel = new JLabel("Use Unigrams:");
        GridBagConstraints rougeSUseUnigramsConstraints = new GridBagConstraints();
        rougeSUseUnigramsConstraints.gridx = x++;
        rougeSUseUnigramsConstraints.gridy = y;
        rougeSUseUnigramsConstraints.insets = new Insets(0, 20, 10, 10);
        add(rougeSUseUnigramsLabel, rougeSUseUnigramsConstraints);

        rougeSUseUnigramsCheckBox = new JCheckBox();
        rougeSUseUnigramsCheckBox.setEnabled(false);
        rougeSUseUnigramsCheckBox.addItemListener(this::onRougeSUseUnigramsCheckBoxChanged);
        GridBagConstraints rougeSUseUnigramsCheckBoxConstraints = new GridBagConstraints();
        rougeSUseUnigramsCheckBoxConstraints.gridx = x++;
        rougeSUseUnigramsCheckBoxConstraints.gridy = y++;
        rougeSUseUnigramsCheckBoxConstraints.insets = new Insets(0,0,10, 5);
        add(rougeSUseUnigramsCheckBox, rougeSUseUnigramsCheckBoxConstraints);

        x = 0;
        JLabel rougeSSkipDistanceLabel = new JLabel("Skip Distance:");
        GridBagConstraints rougeSSkipDistanceLabelConstraints = new GridBagConstraints();
        rougeSSkipDistanceLabelConstraints.gridx = x++;
        rougeSSkipDistanceLabelConstraints.gridy = y;
        rougeSSkipDistanceLabelConstraints.insets = new Insets(0, 20, 10, 10);
        add(rougeSSkipDistanceLabel, rougeSSkipDistanceLabelConstraints);

        skipDistance.setEnabled(false);
        skipDistance.addChangeListener(this::onRougeSSkipDistanceChanged);
        skipDistance.setToolTipText("Zero means unlimited skip distance");
        Component spinnerEditor = skipDistance.getEditor();
        JFormattedTextField textField = ((JSpinner.DefaultEditor) spinnerEditor).getTextField();
        textField.setColumns(1);
        GridBagConstraints rougeSSkipDistanceSpinnerConstraints = new GridBagConstraints();
        rougeSSkipDistanceSpinnerConstraints.gridx = x++;
        rougeSSkipDistanceSpinnerConstraints.gridy = y++;
        rougeSSkipDistanceSpinnerConstraints.insets = new Insets(0,0,10, 5);
        add(skipDistance, rougeSSkipDistanceSpinnerConstraints);


        x = 0;
        rougeL = new JLabel("RougeL");
        rougeL.setEnabled(false);
        rougeL.setFont(metricFont);

        metricLabelConstraints.gridx = x++;
        metricLabelConstraints.gridy = y;
        add(rougeL, metricLabelConstraints);

        rightSpringConstraints.gridx = x++;
        rightSpringConstraints.gridy = y++;
        add(new JPanel(), rightSpringConstraints);

        x = 0;
        JLabel rougeLEnabledLabel = new JLabel("Enabled:");
        GridBagConstraints rougeLEnabledConstraints = new GridBagConstraints();
        rougeLEnabledConstraints.gridx = x++;
        rougeLEnabledConstraints.gridy = y;
        rougeLEnabledConstraints.insets = new Insets(0, 20, 10, 10);
        add(rougeLEnabledLabel, rougeLEnabledConstraints);

        rougeLEnabledCheckbox = new JCheckBox();
        rougeLEnabledCheckbox.setEnabled(false);
        rougeLEnabledCheckbox.addItemListener(this::onRougeLCheckBoxChanged);
        GridBagConstraints rougeLEnabledCheckboxConstraints = new GridBagConstraints();
        rougeLEnabledCheckboxConstraints.gridx = x++;
        rougeLEnabledCheckboxConstraints.gridy = y++;
        rougeLEnabledCheckboxConstraints.insets = new Insets(0,0,10, 5);
        add(rougeLEnabledCheckbox, rougeLEnabledCheckboxConstraints);

        x = 0;
        rougeW = new JLabel("RougeW");
        rougeW.setEnabled(false);
        rougeW.setFont(metricFont);

        metricLabelConstraints.gridx = x++;
        metricLabelConstraints.gridy = y;
        add(rougeW, metricLabelConstraints);

        rightSpringConstraints.gridx = x++;
        rightSpringConstraints.gridy = y++;
        add(new JPanel(), rightSpringConstraints);

        x = 0;
        JLabel rougeWEnabledLabel = new JLabel("Enabled:");
        GridBagConstraints rougeWEnabledConstraints = new GridBagConstraints();
        rougeWEnabledConstraints.gridx = x++;
        rougeWEnabledConstraints.gridy = y;
        rougeWEnabledConstraints.insets = new Insets(0, 20, 10, 10);
        add(rougeWEnabledLabel, rougeWEnabledConstraints);

        rougeWEnabledCheckbox = new JCheckBox();
        rougeWEnabledCheckbox.setEnabled(false);
        rougeWEnabledCheckbox.addItemListener(this::onRougeWCheckBoxChanged);
        GridBagConstraints rougeWEnabledCheckboxConstraints = new GridBagConstraints();
        rougeWEnabledCheckboxConstraints.gridx = x++;
        rougeWEnabledCheckboxConstraints.gridy = y++;
        rougeWEnabledCheckboxConstraints.insets = new Insets(0,0,10, 5);
        add(rougeWEnabledCheckbox, rougeWEnabledCheckboxConstraints);

    }

    public RougeSelectionPanelModel getModel() {
        return model;
    }

    public List<JCheckBox> getnGramsCheckBoxes() {
        return nGramsCheckBoxes;
    }

    public JTextField getAdditionalnGrams() {
        return additionalnGrams;
    }

    private void onMetricModelPanelChanged(MetricPanelModelChangedEvent e) {
        boolean rougeEnabled = e.getMetricPanelModel().isRougeEnabled();
        for (JCheckBox nGramsCheckBox : nGramsCheckBoxes) {
            nGramsCheckBox.setEnabled(rougeEnabled);
            additionalnGrams.setEnabled(rougeEnabled);
        }
        rougeLEnabledCheckbox.setEnabled(rougeEnabled);
        rougeWEnabledCheckbox.setEnabled(rougeEnabled);
        rougeSEnabledCheckbox.setEnabled(rougeEnabled);
        rougeSUseUnigramsCheckBox.setEnabled(rougeEnabled);
        skipDistance.setEnabled(rougeEnabled);

        rougeN.setEnabled(rougeEnabled);
        rougeS.setEnabled(rougeEnabled);
        rougeL.setEnabled(rougeEnabled);
        rougeW.setEnabled(rougeEnabled);
    }

    private void onRouge1CheckBoxChanged(ItemEvent itemEvent) {
        onRougeNCheckBoxChanged(itemEvent, 1);
    }

    private void onRouge2CheckBoxChanged(ItemEvent itemEvent) {
        onRougeNCheckBoxChanged(itemEvent, 2);
    }

    private void onRouge3CheckBoxChanged(ItemEvent itemEvent) {
        onRougeNCheckBoxChanged(itemEvent, 3);
    }

    private void onRougeNCheckBoxChanged(ItemEvent itemEvent, int number) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
        RougeSelectionPanelEvent event = new RougeSelectionPanelEvent();
        event.setSelectionType(RougeSelectionPanelEvent.SelectionType.ROUGE_N_STATIC);
        event.setRougeNStatic(number);
        event.setRougeNStaticValue(selected);

        pubSub.publish(event);
    }

    private void onRougeSCheckBoxChanged(ItemEvent itemEvent) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
        RougeSelectionPanelEvent event = new RougeSelectionPanelEvent();
        event.setSelectionType(RougeSelectionPanelEvent.SelectionType.ROUGE_S);
        event.setRougeS(selected);

        pubSub.publish(event);
    }

    private void onRougeSUseUnigramsCheckBoxChanged(ItemEvent itemEvent) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
        RougeSelectionPanelEvent event = new RougeSelectionPanelEvent();
        event.setSelectionType(RougeSelectionPanelEvent.SelectionType.ROUGE_S_UNIGRAMS);
        event.setRougeSUseUnigrams(selected);

        pubSub.publish(event);
    }

    private void onRougeSSkipDistanceChanged(ChangeEvent changeEvent) {
        int skipDistanceValue = (int) skipDistance.getValue();

        RougeSelectionPanelEvent event = new RougeSelectionPanelEvent();
        event.setSelectionType(RougeSelectionPanelEvent.SelectionType.ROUGE_S_SKIP_DISTANCE);
        event.setSkipDistance(skipDistanceValue);

        pubSub.publish(event);
    }

    private void onRougeLCheckBoxChanged(ItemEvent itemEvent) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
        RougeSelectionPanelEvent event = new RougeSelectionPanelEvent();
        event.setSelectionType(RougeSelectionPanelEvent.SelectionType.ROUGE_L);
        event.setRougeL(selected);

        pubSub.publish(event);
    }

    private void onRougeWCheckBoxChanged(ItemEvent itemEvent) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
        RougeSelectionPanelEvent event = new RougeSelectionPanelEvent();
        event.setSelectionType(RougeSelectionPanelEvent.SelectionType.ROUGE_W);
        event.setRougeW(selected);

        pubSub.publish(event);
    }

    public void onAdditionalnGramsSubmit(ActionEvent actionEvent) {
        RougeSelectionPanelEvent event = new RougeSelectionPanelEvent();
        event.setSelectionType(RougeSelectionPanelEvent.SelectionType.ROUGE_N_TEXT);
        event.setnGramFreeText(additionalnGrams.getText());

        pubSub.publish(event);
    }


    private class AdditionalnGramsFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {

        }

        @Override
        public void focusLost(FocusEvent e) {
            onAdditionalnGramsSubmit(null);
        }
    }
}
