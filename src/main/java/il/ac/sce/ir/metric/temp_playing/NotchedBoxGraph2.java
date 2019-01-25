package il.ac.sce.ir.metric.temp_playing;

import il.ac.sce.ir.metric.core.statistics.NotchedBoxData;
import il.ac.sce.ir.metric.core.utils.math.RangeMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.text.DecimalFormat;

public class NotchedBoxGraph2 extends JPanel {

    double[] data =
            {1, 58,  63,  69,  72,  74,  79,
                    88,  88,  90,  91,  93,  94,
                    97,  97,  99,  99,  99,  100,
                    103, 104, 105, 107, 118, 127,
                    134, 146, 181, 250, 255, 271,
                    288, 288, 281, 282, 300};

    /*double[] data =
            {58,  63,  69,  72,  74,  79,
                    88,  88,  90,  91,  93,  94,
                    97,  97,  99,  99,  99,  100,
                    103, 104, 105, 107, 118, 127};*/

    NotchedBoxData notchedBoxData;

    private final double sizeY = 800;

    private String dataAxeName;

    private double dataScaleMin;

    private double dataScaleMax;

    private double dataScaleStep;

    private int borderPx;

    public NotchedBoxGraph2() {

        System.out.println("data length" + data.length);
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i] / data[data.length - 1];
        }

        notchedBoxData = new NotchedBoxData(data);
        notchedBoxData.adjucentValues();
        notchedBoxData.notches();

        System.out.println("up index: " + notchedBoxData.getUpIndex());
        System.out.println("low index: " + notchedBoxData.getLowIndex());

        System.out.println("up notch: " + notchedBoxData.getUpNotch());
        System.out.println("low notch: " + notchedBoxData.getLowNotch());

        System.out.println("first quartile: " + notchedBoxData.getQuantile().getFirstQuartile());
        System.out.println("third quartile: " + notchedBoxData.getQuantile().getThirdQuartile());

        System.out.println("median: " + notchedBoxData.getQuantile().getMedian());
    }

    public void setBorderPx(int borderPx) {
        this.borderPx = borderPx;
    }

    public int getBorderPx() {
        return borderPx;
    }

    public String getDataAxeName() {
        return dataAxeName;
    }

    public void setDataAxeName(String dataAxeName) {
        this.dataAxeName = dataAxeName;
    }

    public double getDataScaleMin() {
        return dataScaleMin;
    }

    public void setDataScaleMin(double dataScaleMin) {
        this.dataScaleMin = dataScaleMin;
    }

    public double getDataScaleMax() {
        return dataScaleMax;
    }

    public void setDataScaleMax(double dataScaleMax) {
        this.dataScaleMax = dataScaleMax;
    }

    public double getDataScaleStep() {
        return dataScaleStep;
    }

    public void setDataScaleStep(double dataScaleStep) {
        this.dataScaleStep = dataScaleStep;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform at = new AffineTransform();
        Dimension size = getSize();
        at.translate(borderPx, size.getHeight() - borderPx);
        at.scale(1,-1);
        double maxScreenY = size.height - 2 * borderPx;
        double maxScreenX = size.width - 2 * borderPx;
        RangeMapper rX = new RangeMapper(0, 1, 0, maxScreenX);
        RangeMapper rY = new RangeMapper(0, 1, 0, maxScreenY);
        drawAxes(g2, at, rX, rY, maxScreenX, maxScreenY);
    }

    private void drawAxes(Graphics2D g2, AffineTransform screenTransform, RangeMapper rX, RangeMapper rY, double maxScreenX, double maxScreenY) {
        AffineTransform original = g2.getTransform();
        g2.transform(screenTransform);

        Line2D l = new Line2D.Double(0,0,maxScreenX, 0);
        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.BLUE);
        g2.draw(l);
        l = new Line2D.Double(0,0,0, maxScreenY);
        /*g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.RED);*/
        g2.draw(l);

        AffineTransform current = g2.getTransform();
        DecimalFormat df2 = new DecimalFormat( "#,###,###,##0.0" );
        Line2D yAxesMeasure = new Line2D.Double(0, 0, 10, 0);
        for (double t = dataScaleMin; t <= dataScaleMax; t += dataScaleStep) {
            double newX = -5;
            double newY = rY.map(t);
            g2.translate(newX, newY);
            g2.draw(yAxesMeasure);
            String s = df2.format(t);
            FontRenderContext fontRenderContext = g2.getFontRenderContext();
            Rectangle2D stringBounds = g2.getFont().getStringBounds(s, fontRenderContext);
            g2.translate(-(stringBounds.getWidth() + 5), -stringBounds.getHeight() / 4);
            // g2.drawRect(0,0, (int)stringBounds.getWidth(), (int)stringBounds.getHeight());
            g2.scale(1, -1);
            g2.drawString(s, 0,0);
            // System.out.println(s);
            g2.setTransform(current);
        }

        int n = 2;
        Line2D xAxesMeasureLine = new Line2D.Double(0, 0, 0, 10);
        current = g2.getTransform();
        for (double i = 1; i < n; i++) {
            g2.translate(rX.map( i / n), -5.0d);
            g2.draw(xAxesMeasureLine);
            g2.translate(0, 5d);

            double min = data[notchedBoxData.getLowIndex()];
            double minScreen = rY.map(min);
            Line2D minLine = new Line2D.Double(-5, minScreen, 5, minScreen);
            g2.draw(minLine);

            Stroke dashedStroke = new BasicStroke(1,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                    new float[] { 2, 2 }, 0);
            Stroke currentStroke = g2.getStroke();
            g2.setStroke(dashedStroke);

            double q25Screen = rY.map(notchedBoxData.getQuantile().getFirstQuartile());
            Line2D lowDashed = new Line2D.Double(0, minScreen, 0, q25Screen);
            g2.draw(lowDashed);
            g2.setStroke(currentStroke);

            Line2D q25Line = new Line2D.Double(-20, q25Screen, 20, q25Screen);
            g2.draw(q25Line);

            double lowNotchScreen = rY.map(notchedBoxData.getLowNotch());
            Line2D q25ToLowNotchLeftLine = new Line2D.Double(-20, q25Screen, -20, lowNotchScreen);
            Line2D q25ToLowNotchRightLine = new Line2D.Double(20, q25Screen, 20, lowNotchScreen);
            g2.draw(q25ToLowNotchLeftLine);
            g2.draw(q25ToLowNotchRightLine);

            double medianScreen = rY.map(notchedBoxData.getQuantile().getMedian());
            Line2D lowNotchToMedianLeftLine = new Line2D.Double(-20, lowNotchScreen, -10, medianScreen);
            Line2D lowNotchToMedianRightLine = new Line2D.Double(20, lowNotchScreen, 10, medianScreen);
            g2.draw(lowNotchToMedianLeftLine);
            g2.draw(lowNotchToMedianRightLine);

            Line2D medianLine = new Line2D.Double(-10, medianScreen, 10, medianScreen);
            g2.draw(medianLine);

            double upNotchScreen = rY.map(notchedBoxData.getUpNotch());
            Line2D upNotchToMedianLeftLine = new Line2D.Double(-20, upNotchScreen, -10, medianScreen);
            Line2D upNotchToMedianRightLine = new Line2D.Double(20, upNotchScreen, 10, medianScreen);
            g2.draw(upNotchToMedianLeftLine);
            g2.draw(upNotchToMedianRightLine);

            double q75Screen = rY.map(notchedBoxData.getQuantile().getThirdQuartile());
            Line2D q75ToUpNotchLeftLine = new Line2D.Double(-20, q75Screen, -20, upNotchScreen);
            Line2D q75ToUpNotchRightLine = new Line2D.Double(20, q75Screen, 20, upNotchScreen);
            g2.draw(q75ToUpNotchLeftLine);
            g2.draw(q75ToUpNotchRightLine);

            Line2D q75Line = new Line2D.Double(-20, q75Screen, 20, q75Screen);
            g2.draw(q75Line);

            currentStroke = g2.getStroke();
            g2.setStroke(dashedStroke);
            double max = data[notchedBoxData.getUpIndex()];
            double maxScreen = rY.map(max);
            Line2D maxDashed = new Line2D.Double(0, maxScreen, 0, q75Screen);
            g2.draw(maxDashed);
            g2.setStroke(currentStroke);

            Line2D maxLine = new Line2D.Double(-5, maxScreen, 5, maxScreen);
            g2.draw(maxLine);

            Paint currentPaint = g2.getPaint();
            currentStroke = g2.getStroke();
            g2.setPaint(Color.RED);
            g2.setStroke(new BasicStroke(5));
            for (int j = 0; j < notchedBoxData.getLowIndex(); j++) {
                double lowOutlier = data[j];
                double screenValY = rY.map(lowOutlier);
                Line2D lowOutlierScreenPointY = new Line2D.Double(0, screenValY, 0, screenValY);
                g2.draw(lowOutlierScreenPointY);
            }
            for (int j = notchedBoxData.getUpIndex(); j < data.length; j++) {
                double upOutlier = data[j];
                double screenValY = rY.map(upOutlier);
                Line2D upOutlierScreenPointY = new Line2D.Double(0, screenValY, 0, screenValY);
                g2.draw(upOutlierScreenPointY);
            }
            g2.setPaint(currentPaint);
            g2.setStroke(currentStroke);


            g2.setTransform(current);
        }



        g2.setTransform(original);
    }

    public static void main(String[] args) {
        double d = 0.99983924738248237498327498d;
        System.out.printf("%e \n", d);
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            NotchedBoxGraph2 comp = new NotchedBoxGraph2();
            comp.setBorderPx(30);
            comp.setDataScaleMin(0);
            comp.setDataScaleMax(1);
            comp.setDataScaleStep(0.1d);
            frame.add(comp);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
