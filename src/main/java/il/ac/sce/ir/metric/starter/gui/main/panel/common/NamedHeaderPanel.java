package il.ac.sce.ir.metric.starter.gui.main.panel.common;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class NamedHeaderPanel extends JPanel {

    private final String name;

    public NamedHeaderPanel(String name) {
        if (name == null) {
            this.name = "NoName";
        } else {
            this.name = name;
        }
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;

        Border lineBorder = BorderFactory.createMatteBorder(2, 0, 0, 0, Color.LIGHT_GRAY);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, name);
        setBorder(titledBorder);

    }
}
