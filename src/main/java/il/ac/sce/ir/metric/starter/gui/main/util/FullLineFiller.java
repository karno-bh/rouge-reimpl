package il.ac.sce.ir.metric.starter.gui.main.util;

import java.awt.*;

public class FullLineFiller {

    public GridBagConstraints fullLine(int lineY) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = lineY;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        return constraints;
    }
}
