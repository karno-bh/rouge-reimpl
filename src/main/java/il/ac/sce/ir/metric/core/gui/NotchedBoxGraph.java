package il.ac.sce.ir.metric.core.gui;

import il.ac.sce.ir.metric.core.gui.data.MultiNotchedBoxData;
import il.ac.sce.ir.metric.core.statistics.NotchedBoxData;
import il.ac.sce.ir.metric.core.utils.math.RangeMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class NotchedBoxGraph extends JPanel {

    private MultiNotchedBoxData multiNotchedBoxData;

    private final double sizeY = 800;

    private String graphName;

    private int borderPx;

    public NotchedBoxGraph() {}

    public void setBorderPx(int borderPx) {
        this.borderPx = borderPx;
    }

    public int getBorderPx() {
        return borderPx;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public void setMultiNotchedBoxData(MultiNotchedBoxData multiNotchedBoxData) {
        this.multiNotchedBoxData = multiNotchedBoxData;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 300);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        drawName(g2);
        Dimension size = getSize();
        AffineTransform at = new AffineTransform();
        at.translate(borderPx, size.getHeight() - borderPx);
        at.scale(1,-1);
        double maxScreenY = size.height - 2 * borderPx;
        double maxScreenX = size.width - 2 * borderPx;
        RangeMapper rX = new RangeMapper(0, 1, 0, maxScreenX);
        double[] shrunkMinMax = multiNotchedBoxData.getShrunkMinMax(1.05d);
        RangeMapper rY = new RangeMapper(shrunkMinMax[0], shrunkMinMax[1], 0, maxScreenY);
        drawGraph(g2, at, rX, rY, maxScreenX, maxScreenY);
    }

    private void drawName(Graphics2D g2) {

        // g2 is original here, before "axes world" transformation
        FontRenderContext fontRenderContext = g2.getFontRenderContext();
        Rectangle2D graphNameBounds = g2.getFont().getStringBounds(graphName, fontRenderContext);
        double graphNameHalfWidth = graphNameBounds.getWidth() / 2;
        Dimension size = getSize();
        double viewPortHalfPoint = size.getWidth() / 2;
        double textRenderPoint = viewPortHalfPoint - graphNameHalfWidth;
        g2.drawString(graphName, (float) textRenderPoint, (float) graphNameBounds.getHeight());
    }

    private void drawGraph(Graphics2D g2, AffineTransform screenTransform, RangeMapper rX, RangeMapper rY, double maxScreenX, double maxScreenY) {
        AffineTransform currentTransform;
        Paint currentPaint;
        Stroke currentStroke;

        AffineTransform original = g2.getTransform();
        g2.transform(screenTransform);

        g2.setStroke(new BasicStroke(2));
        g2.setPaint(Color.BLACK);
        Line2D xAxesLine = new Line2D.Double(0,0,maxScreenX, 0);
        g2.draw(xAxesLine);
        Line2D yAxesLine = new Line2D.Double(0,0,0, maxScreenY);
        g2.draw(yAxesLine);

        Line2D yAxesScaleNotchLine = new Line2D.Double(0, 0, 10, 0);
        List<BigDecimal> scale = multiNotchedBoxData.generateScale();

        currentTransform = g2.getTransform();
        currentPaint = g2.getPaint();
        currentStroke = g2.getStroke();
        BasicStroke greedStroke = new BasicStroke(1);
        Line2D greedLine = new Line2D.Double(0, 0, maxScreenX, 0);
        for (BigDecimal scaleVal : scale) {
            double newY = rY.map(scaleVal.doubleValue());
            g2.setPaint(Color.LIGHT_GRAY);
            g2.setStroke(greedStroke);
            g2.translate(0, newY);
            g2.draw(greedLine);

            g2.setTransform(currentTransform);
            g2.setStroke(currentStroke);
            g2.setPaint(currentPaint);
        }

        for (BigDecimal scaleVal : scale) {
            double newX = -5;
            double newY = rY.map(scaleVal.doubleValue());
            g2.translate(newX, newY);
            g2.draw(yAxesScaleNotchLine);
            String s = scaleVal.toString();
            FontRenderContext fontRenderContext = g2.getFontRenderContext();
            Rectangle2D stringBounds = g2.getFont().getStringBounds(s, fontRenderContext);
            g2.translate(-(stringBounds.getWidth() + 5), -stringBounds.getHeight() / 4);
            g2.scale(1, -1);
            g2.drawString(s, 0,0);
            g2.setTransform(currentTransform);
        }


        Line2D xAxesScaleNotchLine = new Line2D.Double(0, 0, 0, 10);
        currentTransform = g2.getTransform();

        double i = 0;
        double n = multiNotchedBoxData.getBoxesByLabel().size() + 1;

        for (Map.Entry<String, NotchedBoxData> labeledNotchedBox :  multiNotchedBoxData.getBoxesByLabel().entrySet()) {
            i++; // from 1 to n - 1
            String notchedBoxLabel = labeledNotchedBox.getKey();
            NotchedBoxData notchedBoxData = labeledNotchedBox.getValue();
            double[] data = notchedBoxData.getData();
            double translateFraction = rX.map(i / n);
            g2.translate(translateFraction, -5.0d);
            g2.draw(xAxesScaleNotchLine);

            FontRenderContext fontRenderContext = g2.getFontRenderContext();
            Rectangle2D labelBounds = g2.getFont().getStringBounds(notchedBoxLabel, fontRenderContext);
            double labelHalfWidth = labelBounds.getWidth() / 2;
            AffineTransform innerCurrentTransform = g2.getTransform();
            g2.scale(1, -1);
            g2.drawString(notchedBoxLabel, (float)(-labelHalfWidth), (float)(labelBounds.getHeight()));
            g2.setTransform(innerCurrentTransform);
            g2.translate(0, 5d);

            double min = data[notchedBoxData.getLowIndex()];
            double minScreen = rY.map(min);
            Line2D minLine = new Line2D.Double(-5, minScreen, 5, minScreen);
            g2.draw(minLine);

            Stroke dashedStroke = new BasicStroke(1,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
                    new float[] { 2, 2 }, 0);
            currentStroke = g2.getStroke();
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

            currentPaint = g2.getPaint();
            currentStroke = g2.getStroke();
            g2.setPaint(Color.RED);
            g2.setStroke(new BasicStroke(5));
            for (int j = 0; j < notchedBoxData.getLowIndex(); j++) {
                double lowOutlier = data[j];
                double screenValY = rY.map(lowOutlier);
                Line2D lowOutlierScreenPointY = new Line2D.Double(0, screenValY, 0, screenValY);
                g2.draw(lowOutlierScreenPointY);
            }
            for (int j = notchedBoxData.getUpIndex() + 1; j < data.length; j++) {
                double upOutlier = data[j];
                double screenValY = rY.map(upOutlier);
                Line2D upOutlierScreenPointY = new Line2D.Double(0, screenValY, 0, screenValY);
                g2.draw(upOutlierScreenPointY);
            }
            g2.setPaint(currentPaint);
            g2.setStroke(currentStroke);
            g2.setTransform(currentTransform);
        }

        g2.setTransform(original);
    }
}
