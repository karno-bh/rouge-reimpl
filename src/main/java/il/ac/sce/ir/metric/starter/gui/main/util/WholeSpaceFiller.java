package il.ac.sce.ir.metric.starter.gui.main.util;

import javax.swing.*;
import java.awt.*;

public class WholeSpaceFiller {

    public GridBagConstraints getFillingConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        return constraints;
    }
}
