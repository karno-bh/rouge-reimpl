package il.ac.sce.ir.metric.starter.gui.main.panel.applicative;

import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;
import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;

import javax.swing.*;
import java.awt.*;

public class ScrollableMetricPanelWrapper extends JPanel {

    private final PubSub pubSub;

    private final MetricPanel metricPanel;

    public ScrollableMetricPanelWrapper(PubSub pubSub) {
        this.pubSub = pubSub;
        metricPanel = new MetricPanel(pubSub);
        setLayout(new GridBagLayout());
        WholeSpaceFiller spaceFiller = new WholeSpaceFiller();
        GridBagConstraints metricPanelConstraints = spaceFiller.getFillingConstraints();
        add(new JScrollPane(metricPanel), metricPanelConstraints);
    }


}
