package il.ac.sce.ir.metric.starter.gui.main.panel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MetricPanel extends JPanel {

    private final FileChoosePanel fileChooserPanel;

    public MetricPanel() {
        this.fileChooserPanel = new FileChoosePanel();
        setLayout(new GridBagLayout());
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        setBorder(emptyBorder);

        GridBagConstraints fileLine = new GridBagConstraints();
        fileLine.gridx = 0;
        fileLine.gridy = 0;
        fileLine.weightx = 1;
        fileLine.fill = GridBagConstraints.HORIZONTAL;
        // fileLine.anchor = GridBagConstraints.FIRST_LINE_START;
        add(fileChooserPanel, fileLine);

        for (int i = 1; i < 150; i++) {
            GridBagConstraints dummy = new GridBagConstraints();
            dummy.gridx = 0;
            dummy.gridy = i;
            dummy.weightx = 1;
            dummy.fill = GridBagConstraints.HORIZONTAL;
            dummy.insets = new Insets(10, 0,0,0);
            add(new JButton("Dummy Button: " + i), dummy);
        }

        GridBagConstraints spring = new GridBagConstraints();
        spring.gridx = 0;
        spring.gridy = 1000;
        spring.weightx = 1;
        spring.weighty = 1;
        spring.fill = GridBagConstraints.BOTH;

        JPanel springPanel = new JPanel();
//        springPanel.setBorder(BorderFactory.createEtchedBorder());
        add(springPanel, spring);
    }


}
