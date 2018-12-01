package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.starter.gui.main.event.model_event.MetricPanelModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.model.MetricPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.FileChoosePanel;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.MetricEnabledPanel;
import il.ac.sce.ir.metric.starter.gui.main.panel.common.NamedHeaderPanel;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;
import il.ac.sce.ir.metric.starter.gui.main.resources.GUIConstants;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class MetricPanel extends JPanel {

    private final PubSub pubSub;

    private final FileChoosePanel workingSetDirectoryChooserPanel;
    private final JButton goButton;

    private final MetricPanelModel metricPanelModel;

    public MetricPanel(PubSub pubSub) {
        this.pubSub = pubSub;

        pubSub.subscribe(MetricPanelModelChangedEvent.class, this::onMetricPanelModelChanged);

        metricPanelModel = new MetricPanelModel(pubSub);
        this.workingSetDirectoryChooserPanel = new FileChoosePanel(pubSub, GUIConstants.EVENT_WORKING_SET_DIRECTORY_CHOSE_PANEL, null);
        this.goButton = new JButton("Start");
        goButton.setEnabled(false);
        setLayout(new GridBagLayout());
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        setBorder(emptyBorder);
        final Insets lineInsets = new Insets(0, 0, 20, 0);
        //final Insets headerInsets = new Insets(0, 0, 0, 0);

        int y = 0;
        GridBagConstraints workingSetHeaderConstraints = new GridBagConstraints();
        workingSetHeaderConstraints.gridx = 0;
        workingSetHeaderConstraints.gridy = y++;
        workingSetHeaderConstraints.weightx = 1;
        workingSetHeaderConstraints.fill = GridBagConstraints.HORIZONTAL;
        // workingSetHeaderConstraints.insets = headerInsets;
        add(new NamedHeaderPanel("Working Set Directory"), workingSetHeaderConstraints);


        GridBagConstraints fileLine = new GridBagConstraints();
        fileLine.gridx = 0;
        fileLine.gridy = y++;
        fileLine.weightx = 1;
        fileLine.fill = GridBagConstraints.HORIZONTAL;
        // fileLine.anchor = GridBagConstraints.FIRST_LINE_START;
        fileLine.insets = lineInsets;
        add(workingSetDirectoryChooserPanel, fileLine);

        GridBagConstraints rougeHeaderConstraints = new GridBagConstraints();
        rougeHeaderConstraints.gridx = 0;
        rougeHeaderConstraints.gridy = y++;
        rougeHeaderConstraints.weightx = 1;
        rougeHeaderConstraints.fill = GridBagConstraints.HORIZONTAL;
        // rougeHeaderConstraints.insets = headerInsets;
        add(new NamedHeaderPanel("Rouge"), rougeHeaderConstraints);

        GridBagConstraints rougeEnabledConstraints = new GridBagConstraints();
        rougeEnabledConstraints.gridx = 0;
        rougeEnabledConstraints.gridy = y++;
        rougeEnabledConstraints.weightx = 1;
        rougeEnabledConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(new MetricEnabledPanel(pubSub, GUIConstants.EVENT_ROUGE_METRIC_SELECTED), rougeEnabledConstraints);

        RougeSelectionPanel rougeSelectionPanel = new RougeSelectionPanel(pubSub);
        // metricPanelModel.setRougeSelectionPanelModel(rougeSelectionPanel.getModel());
        GridBagConstraints rougeSelectionConstraints = new GridBagConstraints();
        rougeSelectionConstraints.gridx = 0;
        rougeSelectionConstraints.gridy = y++;
        rougeSelectionConstraints.weightx = 1;
        rougeSelectionConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(rougeSelectionPanel, rougeSelectionConstraints);

        GridBagConstraints readabilityHeaderConstraints = new GridBagConstraints();
        readabilityHeaderConstraints.gridx = 0;
        readabilityHeaderConstraints.gridy = y++;
        readabilityHeaderConstraints.weightx = 1;
        readabilityHeaderConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(new NamedHeaderPanel("Readability"), readabilityHeaderConstraints);

        GridBagConstraints readabilityEnabledConstraints = new GridBagConstraints();
        readabilityEnabledConstraints.gridx = 0;
        readabilityEnabledConstraints.gridy = y++;
        readabilityEnabledConstraints.weightx = 1;
        readabilityEnabledConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(new MetricEnabledPanel(pubSub, GUIConstants.EVENT_READABILITY_METRIC_SELECTED), readabilityEnabledConstraints);



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

    private void onMetricPanelModelChanged(MetricPanelModelChangedEvent e) {
        // MetricPanelModelChangedEvent e = (MetricPanelModelChangedEvent) event;
        MetricPanelModel metricPanelModel = e.getMetricPanelModel();
        String dirName = metricPanelModel.getChosenMetricsDirectory();
        boolean goButtonEnabled = false;
        if (dirName != null) {
            File dir = new File(dirName);
            if (dir.isDirectory() && (metricPanelModel.isRougeEnabled() || metricPanelModel.isReadabilityEnabled())) {
                goButtonEnabled = true;
            }
        }
        goButton.setEnabled(goButtonEnabled);
    }


}
