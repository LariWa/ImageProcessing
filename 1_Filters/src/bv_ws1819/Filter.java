// BV Ue1 WS2018/19 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1819;

import java.util.Arrays;

public class Filter {

	public enum FilterType {
		COPY("Copy Image"), BOX("Box Filter"), MEDIAN("Median Filter");

		private final String name;

		private FilterType(String s) {
			name = s;
		}

		public String toString() {
			return this.name;
		}
	};

	public enum BorderProcessing {
		CONTINUE("Border: Constant Continuation"), WHITE("Border: White");

		private final String name;

		private BorderProcessing(String s) {
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

	public void box(RasterImage src, RasterImage dst, int kernelSize, BorderProcessing borderProcessing) {
		// TODO: implement a box filter with given kernel size and border processing
		for (int i = 0; i < src.argb.length; i++) {
			int y = (int) (i / src.width);
			int x = i % src.width;
			int[] kernel = getKernel(x, y, kernelSize, borderProcessing, src);
			int sum = 0;
			for (int j = 0; j < kernel.length; j++) {
				sum += kernel[j];
			}
			int value = (int) (sum / kernel.length);
			int rgb = 0xff000000 | (value << 16) | (value << 8) | value;
			dst.argb[i] = rgb;
		}

	}

	public void median(RasterImage src, RasterImage dst, int kernelSize, BorderProcessing borderProcessing) {
		// TODO: implement a median filter with given kernel size and border processing
		for (int i = 0; i < src.argb.length; i++) {
			int y = (int) (i / src.width);
			int x = i % src.width;
			int[] kernel = getKernel(x, y, kernelSize, borderProcessing, src);
			int median = 0;
			Arrays.sort(kernel);
			median = kernel[kernel.length/2];
			int rgb = 0xff000000 | (median << 16) | (median << 8) | median;
			dst.argb[i] = rgb;
		}
	}

	public int[] getKernel(int x, int y, int kernelSize, BorderProcessing borderProcessing, RasterImage src) {
		int[] kernel = new int[kernelSize * kernelSize];
		int npos = (y - (int) ((kernelSize) / 2)) * src.width + x - (int) (kernelSize / 2);
		for (int yn = 0; yn < kernelSize; yn++) {
			for (int xn = 0; xn < kernelSize; xn++) {
				int pos = npos + src.width * yn + xn;
				if (x <= (int) kernelSize / 2 || y <= (int) kernelSize / 2 || x >= src.width - 1 - (int) kernelSize / 2
						|| y >= src.height - 1 - (int) kernelSize / 2) {

					if (borderProcessing.equals(BorderProcessing.CONTINUE)) {
						kernel[yn * kernelSize + xn] = getValue(src.argb[y * src.width + x]);

					} else
						kernel[yn * kernelSize + xn] = 0xffffffff;
				}

				else {
					kernel[yn * kernelSize + xn] = getValue(src.argb[pos]);
				}
			}
		}

		return kernel;

	}

	public int getValue(int rgb) {
		int g = (rgb >> 16) & 0xff;
		return g;

	}
}
