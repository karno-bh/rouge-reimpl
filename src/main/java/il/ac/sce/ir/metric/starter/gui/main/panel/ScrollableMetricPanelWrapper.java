package il.ac.sce.ir.metric.starter.gui.main.panel;

import il.ac.sce.ir.metric.starter.gui.main.util.WholeSpaceFiller;

import javax.swing.*;
import java.awt.*;

public class ScrollableMetricPanelWrapper extends JPanel {

    private final MetricPanel metricPanel;

    public ScrollableMetricPanelWrapper() {
        metricPanel = new MetricPanel();
        setLayout(new GridBagLayout());
        WholeSpaceFiller spaceFiller = new WholeSpaceFiller();
        GridBagConstraints metricPanelConstraints = spaceFiller.getFillingConstraints();
        add(new JScrollPane(metricPanel), metricPanelConstraints);
    }


}
