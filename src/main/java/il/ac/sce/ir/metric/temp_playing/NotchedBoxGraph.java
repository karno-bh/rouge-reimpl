package il.ac.sce.ir.metric.temp_playing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

public class NotchedBoxGraph extends JPanel {

    public NotchedBoxGraph() {

    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // System.out.println("Called!");
        Graphics2D g2 = (Graphics2D) g;
        g2.drawString("This is my custom Panel! Size: " + getSize(),10,20);
        g2.setPaint(Color.RED);
        g2.setStroke(new BasicStroke(4));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        path.moveTo(50, 50);
        path.lineTo(70, 44);
        path.curveTo(100, 10, 140, 80, 160, 80);
        path.lineTo(190, 40);
        path.lineTo(200, 56);
        path.quadTo(100, 150, 70, 60);
        path.closePath();
        g2.draw(path);
        g2.setPaint(Color.WHITE);
        g2.fill(path);

        Ellipse2D e = new Ellipse2D.Float(40, 40, 120, 120);
        GradientPaint gp = new GradientPaint(75, 75, Color.white,
                95, 95, Color.gray, true);
        g2.setPaint(gp);
        g2.fill(e);

        g2.setPaint(Color.BLACK);
        Rectangle2D r = new Rectangle2D.Double(50, 50, 100, 100);
        Stroke stroke = new BasicStroke(2,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0,
                new float[] { 4, 4 }, 0);
        g2.setStroke(stroke);
        g2.draw(r);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.add(new NotchedBoxGraph());
            frame.pack();
            frame.setVisible(true);
        });
    }
}
