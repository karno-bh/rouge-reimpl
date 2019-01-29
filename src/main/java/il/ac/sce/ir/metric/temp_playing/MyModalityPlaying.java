package il.ac.sce.ir.metric.temp_playing;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class MyModalityPlaying {

    int count = 0;

    JFrame jFrame;

    void start() {
        SwingUtilities.invokeLater(() -> {
            jFrame = new JFrame("Dialog Demo");
            JButton jButton = new JButton("Create New Dialog!");
            jButton.addActionListener(this::newDialogPressed);
            jFrame.add(jButton);

            jFrame.pack();
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
        });
    }

    JDialog createDialog(JFrame owner) {
        JDialog jDialog = new JDialog(owner, "Some Title!");
        JButton jButton = new JButton("Dialog " + count++);
        jDialog.add(jButton);
        return jDialog;
    }

    public void newDialogPressed(ActionEvent e) {
        JDialog dialog = createDialog(jFrame);
        dialog.pack();
        dialog.setLocationRelativeTo(jFrame);
        dialog.setVisible(true);
        //
    }

    public static void main(String[] args) {
        MyModalityPlaying myModalityPlaying = new MyModalityPlaying();
        myModalityPlaying.start();
    }
}
