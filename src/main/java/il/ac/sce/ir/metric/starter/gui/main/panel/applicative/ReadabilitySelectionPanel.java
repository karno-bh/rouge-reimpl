package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.ReadabilitySelectionPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.MetricPanelModelChangedEvent;
import il.ac.sce.ir.metric.starter.gui.main.event.model_event.ReadabilitySelectionPanelModelEvent;
import il.ac.sce.ir.metric.starter.gui.main.model.ReadabilitySelectionPanelModel;
import il.ac.sce.ir.metric.starter.gui.main.util.ModelsManager;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class ReadabilitySelectionPanel extends JPanel {

    private final PubSub pubSub;

    private final ReadabilitySelectionPanelModel model;

    private final JCheckBox peersEnabledCheckBox;

    private final JCheckBox topicsEnabledCheckBox;

    public ReadabilitySelectionPanel(PubSub pubSub, ModelsManager modelsManager) {
        this.pubSub = pubSub;
        this.model = new ReadabilitySelectionPanelModel(pubSub);
        pubSub.subscribe(MetricPanelModelChangedEvent.class, this::onMetricPanelModelChangedEvent);
        modelsManager.register(model);

        setLayout(new GridBagLayout());

        GridBagConstraints rightSpring = new GridBagConstraints();
        rightSpring.weightx = 1;
        rightSpring.fill = GridBagConstraints.HORIZONTAL;

        Insets labelInsets = new Insets(0, 20, 10, 10);
        Insets checkBoxInsets = new Insets(0,0,10, 5);

        int x = 0, y = 0;
        JLabel peersLabel = new JLabel("Peers:");
        GridBagConstraints peersLabelConstraints = new GridBagConstraints();
        peersLabelConstraints.gridx = x++;
        peersLabelConstraints.gridy = y;
        peersLabelConstraints.insets = labelInsets;
        add(peersLabel, peersLabelConstraints);

        peersEnabledCheckBox = new JCheckBox();
        peersEnabledCheckBox.addItemListener(this::onPeersEnabledCheckBoxChanged);
        GridBagConstraints peersEnabledCheckBoxConstraints = new GridBagConstraints();
        peersEnabledCheckBoxConstraints.gridx = x++;
        peersEnabledCheckBoxConstraints.gridy = y;
        peersEnabledCheckBoxConstraints.insets = checkBoxInsets;
        add(peersEnabledCheckBox, peersEnabledCheckBoxConstraints);

        rightSpring.gridx = x++;
        rightSpring.gridy = y++;
        add(new JPanel(), rightSpring);

        x = 0;
        JLabel topicsLabel = new JLabel("Topics:");
        GridBagConstraints topicsLabelConstraints = new GridBagConstraints();
        topicsLabelConstraints.gridx = x++;
        topicsLabelConstraints.gridy = y;
        topicsLabelConstraints.insets = labelInsets;
        add(topicsLabel, topicsLabelConstraints);

        topicsEnabledCheckBox = new JCheckBox();
        topicsEnabledCheckBox.addItemListener(this::onTopicsEnabledCheckBoxChanged);
        GridBagConstraints topicsEnabledCheckBoxConstraints = new GridBagConstraints();
        topicsEnabledCheckBoxConstraints.gridx = x++;
        topicsEnabledCheckBoxConstraints.gridy = y;
        topicsEnabledCheckBoxConstraints.insets = checkBoxInsets;
        add(topicsEnabledCheckBox, topicsEnabledCheckBoxConstraints);

        rightSpring.gridx = x++;
        rightSpring.gridy = y++;
        add(new JPanel(), rightSpring);
    }

    private void onReadabilitySelectionPanelModelChanged(ReadabilitySelectionPanelModelEvent event) {
        // ??? should it initially syncs with model ???
    }

    private void onMetricPanelModelChangedEvent(MetricPanelModelChangedEvent event) {
        boolean readabilityEnabled = event.getMetricPanelModel().isReadabilityEnabled();
        peersEnabledCheckBox.setEnabled(readabilityEnabled);
        topicsEnabledCheckBox.setEnabled(readabilityEnabled);
    }

    private void onPeersEnabledCheckBoxChanged(ItemEvent itemEvent) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;

        ReadabilitySelectionPanelEvent event = new ReadabilitySelectionPanelEvent(
            ReadabilitySelectionPanelEvent.SelectionType.PEERS_SELECTED,
            selected
        );
        pubSub.publish(event);
    }

    private void onTopicsEnabledCheckBoxChanged(ItemEvent itemEvent) {
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;

        ReadabilitySelectionPanelEvent event = new ReadabilitySelectionPanelEvent(
                ReadabilitySelectionPanelEvent.SelectionType.TOPICS_SELECTED,
                selected
        );
        pubSub.publish(event);
    }


}
