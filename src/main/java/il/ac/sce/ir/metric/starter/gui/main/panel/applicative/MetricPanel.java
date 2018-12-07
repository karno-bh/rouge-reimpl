package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.GoButtonClickEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.GoButtonModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.MetricPanelModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.model.GoButtonModel;
import il.ac.sce.ir.metric.starter.gui.main.model.MetricPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.FileChoosePanel;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.MetricEnabledPanel;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.NamedHeaderPanel;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;
import il.ac.sce.ir.metric.starter.gui.main.util.ModelsManager;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MetricPanel extends JPanel {

    private final PubSub pubSub;

    private final FileChoosePanel workingSetDirectoryChooserPanel;
    private final JButton goButton;

    private final MetricPanelModel metricPanelModel;
    private final GoButtonModel goButtonModel;

    public MetricPanel(PubSub pubSub, ModelsManager modelsManager) {
        this.pubSub = pubSub;

//        pubSub.subscribe(MetricPanelModelChangedEvent.class, this::onMetricPanelModelChanged);
        pubSub.subscribe(GoButtonModelChangedEvent.class, this::onGoButtonModelChangedEvent);

        metricPanelModel = new MetricPanelModel(pubSub);
        modelsManager.register(metricPanelModel);
        goButtonModel = new GoButtonModel(pubSub);
        modelsManager.register(goButtonModel);

        this.workingSetDirectoryChooserPanel = new FileChoosePanel(pubSub, GUIConstants.EVENT_WORKING_SET_DIRECTORY_CHOSE_PANEL, null);
        this.goButton = new JButton("Start");
        this.goButton.addActionListener(this::goButtonClicked);
        goButton.setEnabled(false);
        setLayout(new GridBagLayout());
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        setBorder(emptyBorder);
        final Insets lineInsets = new Insets(0, 0, 20, 0);
        //final Insets headerInsets = new Insets(0, 0, 0, 0);

        int y = 0;
        add(new NamedHeaderPanel("Working Set Directory"), fullLine(y++));
        GridBagConstraints fileLine = fullLine(y++);
        fileLine.insets = lineInsets;
        add(workingSetDirectoryChooserPanel, fileLine);

        add(new NamedHeaderPanel("Rouge"), fullLine(y++));
        add(new MetricEnabledPanel(pubSub, GUIConstants.EVENT_ROUGE_METRIC_SELECTED), fullLine(y++));
        add(new RougeSelectionPanel(pubSub, modelsManager), fullLine(y++));

        add(new NamedHeaderPanel("Readability"), fullLine(y++));
        add(new MetricEnabledPanel(pubSub, GUIConstants.EVENT_READABILITY_METRIC_SELECTED), fullLine(y++));
        add(new ReadabilitySelectionPanel(pubSub, modelsManager), fullLine(y++));

        add(new NamedHeaderPanel("Auto Summ ENG"), fullLine(y++));
        add(new MetricEnabledPanel(pubSub, GUIConstants.EVENT_AUTO_SUMM_ENG_METRIC_SELECTED), fullLine(y++));
        add(new AutoSummENGSelectionPanel(pubSub, modelsManager), fullLine(y++));

        /*for (int i = 1; i < 150; i++) {
            GridBagConstraints dummy = new GridBagConstraints();
            dummy.gridx = 0;
            dummy.gridy = i;
            dummy.weightx = 1;
            dummy.fill = GridBagConstraints.HORIZONTAL;
            dummy.insets = new Insets(10, 0,0,0);
            add(new JButton("Dummy Button: " + i), dummy);
        }*/

        y = 1000;
        GridBagConstraints spring = new GridBagConstraints();
        spring.gridx = 0;
        spring.gridy = y++;
        spring.weightx = 1;
        spring.weighty = 1;
        spring.fill = GridBagConstraints.BOTH;

        JPanel springPanel = new JPanel();
//        springPanel.setBorder(BorderFactory.createEtchedBorder());
        add(springPanel, spring);


        GridBagConstraints goButtonConstraints = new GridBagConstraints();
        goButtonConstraints.gridx = 0;
        goButtonConstraints.gridy = y++;
        goButtonConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        add(goButton, goButtonConstraints);
    }

    private void onGoButtonModelChangedEvent(GoButtonModelChangedEvent event) {
        goButton.setEnabled(event.getGoButtonModel().isGoButtonEnabled());
    }

    private void goButtonClicked(ActionEvent actionEvent) {
        GoButtonClickEvent goButtonClickEvent = new GoButtonClickEvent();
        pubSub.publish(goButtonClickEvent);
    }

    private GridBagConstraints fullLine(int lineY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = lineY;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        return constraints;
    }

}
