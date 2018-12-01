package il.ac.sce.ir.metric.starter.gui.main.panel.common;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.MetricEnabledPanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class MetricEnabledPanel extends JPanel {

    private final PubSub pubSub;
    private final String name;

    private final JLabel text;
    private final JCheckBox checkBox;

    public MetricEnabledPanel(PubSub pubSub, String name) {
        this.pubSub = pubSub;
        this.name = name;

        setLayout(new GridBagLayout());
        text = new JLabel("Enabled:");
        checkBox = new JCheckBox();

        checkBox.addItemListener(this::checkBoxValueChanged);

        int x = 0;
        GridBagConstraints leftSpringConstraints = new GridBagConstraints();
        leftSpringConstraints.gridx = x++;
        leftSpringConstraints.gridy = 0;
        leftSpringConstraints.weightx = 1;
        leftSpringConstraints.fill = GridBagConstraints.HORIZONTAL;
        add(new JPanel(), leftSpringConstraints);

        GridBagConstraints textConstraints = new GridBagConstraints();
        textConstraints.gridx = x++;
        textConstraints.gridy = 0;
        textConstraints.insets = new Insets(0,0,0, 5);
        add(text, textConstraints);

        GridBagConstraints checkBoxConstraints = new GridBagConstraints();
        checkBoxConstraints.gridx = x++;
        checkBoxConstraints.gridy = 0;
        add(checkBox, checkBoxConstraints);
    }

    public void checkBoxValueChanged(ItemEvent itemEvent) {
        // JCheckBox checkBox = (JCheckBox) itemEvent.getItem();
        boolean selected = itemEvent.getStateChange() == ItemEvent.SELECTED;
        MetricEnabledPanelEvent e = new MetricEnabledPanelEvent(name, selected);
        pubSub.publish(e);
    }

}
