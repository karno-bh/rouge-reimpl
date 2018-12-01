package il.ac.sce.ir.metric.starter.gui.main.panel.common;

import il.ac.sce.ir.metric.starter.gui.main.event.component_event.FileChoosePanelEvent;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;
import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.PubSub;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FileChoosePanel extends JPanel {

    private final PubSub pubSub;
    private final String name;

    private final JLabel workingDirectoryLabel;
    private final JTextField workingDirectoryTextField;
    private final JButton choose;


    public FileChoosePanel(PubSub pubSub, String name, String label) {
        this.name = name;
        this.pubSub = pubSub;

        pubSub.subscribe(FileChoosePanelEvent.class, this::onFileChooseEvent);
        workingDirectoryLabel = new JLabel(label);
        workingDirectoryTextField = new JTextField();
        // workingDirectoryTextField.setEditable(false);
        workingDirectoryTextField.setText("Please chose a valid directory");
        workingDirectoryTextField.setEditable(false);
        workingDirectoryTextField.setBackground(Color.WHITE);
        choose = new JButton("Choose");
        choose.addActionListener(this::chooseButtonClicked);



        setLayout(new GridBagLayout());

        int x = 0;
        if (label != null) {
            GridBagConstraints labelConstraints = new GridBagConstraints();
            labelConstraints.gridx = x++;
            labelConstraints.gridy = 0;
            labelConstraints.anchor = GridBagConstraints.CENTER;
            labelConstraints.insets = new Insets(5, 5, 5, 5);
            add(workingDirectoryLabel, labelConstraints);
        }

        GridBagConstraints textFieldConstraints = new GridBagConstraints();
        textFieldConstraints.gridx = x++;
        textFieldConstraints.gridy = 0;
        textFieldConstraints.weightx = 1;
        textFieldConstraints.weighty = 1;
        textFieldConstraints.fill = GridBagConstraints.BOTH;
        textFieldConstraints.insets = new Insets(0, 0, 0, 5);
        add(workingDirectoryTextField, textFieldConstraints);

        GridBagConstraints chooseButtonConstraints = new GridBagConstraints();
        chooseButtonConstraints.gridx = x++;
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

    public void chooseButtonClicked(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Chose the directory for processing");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        String chosenDirAbsolutePath = null;
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chosenDirAbsolutePath = fileChooser.getSelectedFile().getAbsolutePath();
//            workingDirectoryTextField.setText(chosenDirAbsolutePath);
        }
        FileChoosePanelEvent fileChoosePanelEvent = new FileChoosePanelEvent(this.name, chosenDirAbsolutePath);
        pubSub.publish(fileChoosePanelEvent);
    }

    public void onFileChooseEvent(Event appEvent) {
        FileChoosePanelEvent event = (FileChoosePanelEvent) appEvent;
        if (name.equals(event.getSource()) && event.getFileName() != null) {
            workingDirectoryTextField.setText(event.getFileName());
        }
    }
}
