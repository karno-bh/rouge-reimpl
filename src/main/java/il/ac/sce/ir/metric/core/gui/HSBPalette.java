package il.ac.sce.ir.metric.core.gui;

import java.awt.Color;

public class HSBPalette {
	
	public static Color[] genPalette(int size, Color... colors) {
		
		int ranges = colors.length - 1;
		Color[] pallete = new Color[size];
		for (int i = 0; i < ranges; i++) {
			int colorsInRange = size / ranges;
			int from = i * colorsInRange;
			int to = (i + 1) * colorsInRange;
			if (to >= size) {
				to = size - 1;
			}
			colorsInRange = to - from + 1;
			Color c1 = colors[i];
			Color c2 = colors[i + 1];
			
			float[] c1HSBval = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);			
			float[] c2HSBval = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
			
			for (int j = from; j <= to; j++) {
				int k = j - from;
				float percentage = k / (float) (colorsInRange);
				float h = c1HSBval[0] + percentage * (c2HSBval[0] - c1HSBval[0]);
				float s = c1HSBval[1] + percentage * (c2HSBval[1] - c1HSBval[1]);
				float b = c1HSBval[2] + percentage * (c2HSBval[2] - c1HSBval[2]);
				pallete[j] = Color.getHSBColor(h, s, b);
			}
		}
		
		return pallete;
	}
	
	public static int[] mapToInts(Color... colors) {
		int[] intColors = new int[colors.length];
		for(int i = colors.length; i-->0;) intColors[i] = colors[i].getRGB();
		return intColors;
	}
	
}