// BV Ue4 WS2018/19 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-16

package bv_ws1819;

import java.util.Arrays;

import bv_ws1819.ImageAnalysisAppController.StatsProperty;
import javafx.collections.ObservableList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Histogram {

	private static final int grayLevels = 256;

	private GraphicsContext gc;
	private int maxHeight;
	private RasterImage img;

	private int[] histogram = new int[grayLevels];

	public Histogram(GraphicsContext gc, int maxHeight) {
		this.gc = gc;
		this.maxHeight = maxHeight;
	}

	public void update(RasterImage image, Rectangle selectionRect, ObservableList<StatsProperty> statsData) {
		// TODO: calculate histogram[] out of the gray values found in the image's
		// selectionRect
		img = image;
		Arrays.fill(histogram, 0);
		int X = (int) Math.round(selectionRect.getX());
		int Y = (int) Math.round(selectionRect.getY());
		int width = (int) Math.round(selectionRect.getWidth());
		int height = (int) Math.round(selectionRect.getHeight());
		for (int y = Y; y < height + Y; y++) {
			for (int x = X; x < width + X; x++) {
				int pos = y * image.width + x;
				int grey = image.argb[pos] & 0xff;
				histogram[grey]++;
			}

		}
		int min = getMinimum();
		int max = getMaximum();
		for (StatsProperty property : statsData) {
			switch (property) {
			case Minimum:
				property.setValue(min);
				break;
			case Maximum:
				property.setValue(max);
				break;
			case Mean:
				property.setValue(getMittelwert(min, max));
				break;
			case Variance:
				property.setValue(getVarianz(getMittelwert(min, max)));
				break;
			case Median:
				property.setValue(getMedian());
				break;
			case Entropy:
				property.setValue(getEntropy(image.argb.length));
				// System.out.print("entropy!" + getEntropy(image.argb.length) );
				break;

			}

		}
	}

	public int getMinimum() {
		int min = 0;
		for (int i = 0; i <= 255; i++) {
			if (histogram[i] != 0) {
				min = i;
				break;
			}
		}
		return min;
	}

	public int getMaximum() {
		int max = 255;
		for (int i = 255; i >= 0; i--) {
			if (histogram[i] != 0) {
				max = i;
				break;
			}
		}
		return max;
	}

	public double getMittelwert(int min, int max) {
		int mittel = 0;
		for (int i = 0; i < histogram.length; i++) {
			mittel += i * histogram[i];
		}
		return mittel / img.argb.length;
	}

	public double getVarianz(double mittel) {
		int var = 0;
		for (int i = 0; i < img.argb.length; i++) {
			var += Math.pow(((img.argb[i] & 0xff) - mittel), 2);
		}
		return var / img.argb.length;
	}

	public int getMedian() {
		int[] imageSort = new int[img.argb.length];
		for (int i = 0; i < img.argb.length; i++) {
			imageSort[i] = img.argb[i] & 0xff;
		}
		Arrays.sort(imageSort);
		return imageSort[(int) (imageSort.length / 2)];
	}

	public double getEntropy(int n) {
		double entropy = 0;
		for (int i = 0; i < histogram.length; i++) {
			float p = ((float) histogram[i]) / (float) n;
			if (p > 0)
				entropy += p * (Math.log(p) / Math.log(2));
		}
		if (entropy < 0)
			entropy *= -1;

		return entropy;
	}

	public void draw() {
		gc.clearRect(0, 0, grayLevels, maxHeight);
		gc.setLineWidth(1);

		// TODO: draw histogram[] into the gc graphic context
		double shift = 0.5;
		// note that we need to add 0.5 to all coordinates to get a one pixel thin line
		float scale = 0;
		for (int i = 0; i < histogram.length; i++) {
			if (scale < histogram[i])
				scale = histogram[i];
		}
		scale = 200 / scale;
		gc.setStroke(Color.GREEN);
		for (int i = 0; i < histogram.length; i++) {
			gc.strokeLine(i + shift, 200, i + shift, 200 - (histogram[i] * scale));
		}
	}

}
