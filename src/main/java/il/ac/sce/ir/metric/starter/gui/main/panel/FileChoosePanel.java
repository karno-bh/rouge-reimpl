package il.ac.sce.ir.metric.starter.gui.main.panel;

import javax.swing.*;
import java.awt.*;

public class FileChoosePanel extends JPanel {

    private final JLabel workingDirectoryLabel;
    private final JTextField workingDirectoryTextField;
    private final JButton choose;

    public FileChoosePanel() {
        workingDirectoryLabel = new JLabel("Working Set Directory:");
        workingDirectoryTextField = new JTextField();
        // workingDirectoryTextField.setEditable(false);
        workingDirectoryTextField.setText("Here should be a folder!");
        choose = new JButton("Choose");

        setLayout(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.CENTER;
        labelConstraints.insets = new Insets(5, 5, 5, 5);
        add(workingDirectoryLabel, labelConstraints);

        GridBagConstraints textFieldConstraints = new GridBagConstraints();
        textFieldConstraints.gridx = 1;
        textFieldConstraints.gridy = 0;
        textFieldConstraints.weightx = 1;
        textFieldConstraints.weighty = 1;
        textFieldConstraints.fill = GridBagConstraints.BOTH;
        textFieldConstraints.insets = new Insets(0, 0, 0, 5);
        add(workingDirectoryTextField, textFieldConstraints);

        GridBagConstraints chooseButtonConstraints = new GridBagConstraints();
        chooseButtonConstraints.gridx = 2;
        chooseButtonConstraints.gridy = 0;
        chooseButtonConstraints.fill = GridBagConstraints.LINE_END;
        add(choose, chooseButtonConstraints);
    }

    public JLabel getWorkingDirectoryLabel() {
        return workingDirectoryLabel;
    }

    public JTextField getWorkingDirectoryTextField() {
        return workingDirectoryTextField;
    }

    public JButton getChoose() {
        return choose;
    }
}
