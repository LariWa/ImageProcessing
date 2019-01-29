// BV Ue1 WS2018/19 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1819;

import java.util.Arrays;

import javafx.collections.ObservableList;
import javafx.scene.shape.Rectangle;

public class Predictor {
	
	public enum PredictorType {
		A("A (horizontal)"), B("B (vertikal)"), C("C (diagonal)"), ABC("A+B-C"), AB2("(A+B)/2"), adaptiv("adaptiv");

		private final String name;

		private PredictorType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	};

	// filter implementations go here:

	public void copy(RasterImage src, RasterImage dst) {
		for (int i = 0; i < src.argb.length; i++) {
			dst.argb[i] = src.argb[i];
		}

	}

	public float mse(RasterImage orig, RasterImage errorImg) {
		float error2 = 0;
		for(int i = 0; i < orig.argb.length; i++) {
			error2 += Math.pow(getValue(orig.argb[i])-getValue(errorImg.argb[i]),2);
		}
		float mse = error2/orig.argb.length;
		return mse;
	}
	
	public void prediction(RasterImage src, RasterImage dst, RasterImage rec, PredictorType predictior) {
		for (int i = 0; i < src.argb.length; i++) {
			int y = (int) (i / src.width);
			int x = i % src.width;
			int[] predictions = null;
			switch (predictior) {
			case A:
				predictions = a(src, x, y);
				break;
			case B:
				predictions = b(src, x, y);
				break;
			case C:
				predictions = c(src, x, y);
				break;
			case ABC:
				predictions = abc(src, x, y);
				break;
			case AB2:
				predictions = ab2(src, x, y);
				break;
			case adaptiv:
				predictions = adaptiv(src, x, y);
				break;
			default:
				break;
			}
			int error = predictions[0];
			int prediction = predictions[1];
			
			error += 128;
			if (error > 255)
				error = 255;
			if (error < 0)
				error = 0;
			int rgb = 0xff000000 | (error << 16) | (error << 8) | error;
			dst.argb[i] = rgb;

			rgb = 0xff000000 | (prediction << 16) | (prediction << 8) | prediction;
			rec.argb[i] = rgb;
		}
		
	}

	public int[] a(RasterImage src, int x, int y) {
		int error = 0;
		int prediction = 0;
		if (x - 1 < 0) {
			error = getValue(src.argb[x + y * src.width]) - 128;
			prediction = 128;
		} else {
			prediction = getValue(src.argb[(x - 1) + y * src.width]);
			error = getValue(src.argb[x + src.width * y]) - prediction;
		}
		int[] erg = { error, prediction };
		return erg;
	}

	public int[] b(RasterImage src, int x, int y) {
		int error = 0;
		int prediction = 0;
		if (y - 1 < 0) {
			error = getValue(src.argb[x + src.width * y]) - 128;
			prediction = 128;
		} else {
			prediction = getValue(src.argb[(y - 1) * src.width + x]);
			error = getValue(src.argb[x + src.width * y]) - prediction;
		}
		int[] erg = { error, prediction };
		return erg;
	}

	public int[] c(RasterImage src, int x, int y) {
		int error = 0;
		int prediction = 0;
		if ((y - 1 < 0) || (x - 1 < 0)) {
			error = getValue(src.argb[x + src.width * y]) - 128;
			prediction = 128;
		} else {
			prediction = getValue(src.argb[(y - 1) * src.width + x - 1]);
			error = getValue(src.argb[x + src.width * (y)]) - prediction;
		}
		int[] erg = { error, prediction };
		return erg;

	}

	public int[] abc(RasterImage src, int x, int y) {
		int error = 0;
		int prediction = 0;
		int A, B, C;
			if (x - 1 < 0) {
				A = 128;
			} else {
				A = getValue(src.argb[x - 1 + src.width * (y)]);
			}
			if (y - 1 < 0) {
				B = 128;
			} else {
				B = getValue(src.argb[(y - 1) * src.width + x]);
			}
			if ((y - 1 < 0) || (x - 1 < 0)) {
				C = 128;
			} else {
				C = getValue(src.argb[(y - 1) * src.width + x - 1]);
			}
			
			prediction = A + B - C;
			error = getValue(src.argb[x + src.width * y]) - prediction;

		int[] erg = { error, prediction };
		return erg;

	}

	public int[] ab2(RasterImage src, int x, int y) {
		int error = 0;
		int prediction = 0;
		int A, B;
			if (x - 1 < 0) {
				A = 128;
			} else {
				A = getValue(src.argb[x - 1 + src.width * (y)]);
			}
			if (y - 1 < 0) {
				B = 128;
			} else {
				B = getValue(src.argb[(y - 1) * src.width + x]);
			}
			
			prediction = (A + B)/2;
			error = getValue(src.argb[x + src.width * y]) - prediction;

		int[] erg = { error, prediction };
		return erg;
	}

	public int[] adaptiv(RasterImage src, int x, int y) {
		int error = 0;
		int prediction = 0;
		int A, B, C;
			if (x - 1 < 0) {
				A = 128;
			} else {
				A = getValue(src.argb[x - 1 + src.width * (y)]);
			}
			if (y - 1 < 0) {
				B = 128;
			} else {
				B = getValue(src.argb[(y - 1) * src.width + x]);
			}
			if ((y - 1 < 0) || (x - 1 < 0)) {
				C = 128;
			} else {
				C = getValue(src.argb[(y - 1) * src.width + x - 1]);
			}
			
			if (Math.abs(A-C) < Math.abs(B-C)) {
				prediction = B;
			} else {
				prediction = A;
			}
			error = getValue(src.argb[x + src.width * y]) - prediction;

		int[] erg = { error, prediction };
		return erg;
	}

	public double getEntropy(int n, RasterImage src) {
		int[] histogram = histogram(src);
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

	
	public int getValue(int rgb) {
		int g = (rgb >> 16) & 0xff;
		return g;

	}
	public int[] histogram(RasterImage src) {
		int[] histogram = new int[256];
		Arrays.fill(histogram, 0);
		for (int y = 0; y < src.height; y++) {
			for (int x = 0; x < src.width; x++) {
				int pos = y * src.width + x;
				int grey = src.argb[pos] & 0xff;
				histogram[grey]++;
			}

		}
		return histogram;
	}
	
}
