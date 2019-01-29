// BV Ue3 WS2018/19 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1819;

public class MorphologicFilter {

	public enum FilterType {
		DILATION("Dilation"), EROSION("Erosion");

		private final String name;

		private FilterType(String s) {
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

	public void dilation(RasterImage src, RasterImage dst, boolean[][] kernel) {
		// kernel's first dimension: y (row), second dimension: x (column)
		// TODO: dilate the image using the given kernel

		for (int i = 0; i < src.argb.length; i++) {
			if (getValue(src.argb[i]) == 0) {
				int y = (int) (i / src.width);
				int x = i % src.width;
				int[] skernel = getKernel(x, y, src);
				for (int j = 0; j < 5; j++) {
					for (int k = 0; k < 5; k++) {
						if (skernel[j * 5 + k] != -1) {
							if (kernel[j][k]) {
								dst.argb[skernel[j * 5 + k]] = 0xff000000;

							} 
						}

					}
				}
			} else
				dst.argb[i] = 0xffffffff;
		}
	}

	public void erosion(RasterImage src, RasterImage dst, boolean[][] kernel) {
		for (int i = 0; i < src.argb.length; i++) {
			if (getValue(src.argb[i]) == 255) {
				int y = (int) (i / src.width);
				int x = i % src.width;
				int[] skernel = getKernel(x, y, src);
				for (int j = 0; j < 5; j++) {
					for (int k = 0; k < 5; k++) {
						if (skernel[j * 5 + k] != -1) {
							if (kernel[j][k]) {
								dst.argb[skernel[j * 5 + k]] = 0xff000000;

							} 
						}

					}
				}
			} else
				dst.argb[i] = 0xffffffff;
		}
		
		dst.invert();
		// kernel's first dimension: y (row), second dimension: x (column)
		// TODO: erode the image using the given kernel
		
	}

	
	public int[] getKernel(int x, int y, RasterImage src) {
		int[] kernel = new int[25];
		int npos = (y - 2) * src.width + x - 2;
		for (int yn = 0; yn < 5; yn++) {
			for (int xn = 0; xn < 5; xn++) {
				int pos = npos + src.width * yn + xn;
				if (x <= 2 || y <= 2 || x >= src.width - 1 - 2 || y >= src.height - 1 - 2) {
					kernel[yn * 5 + xn] = -1;
				} else
					kernel[yn * 5 + xn] = pos;
			}
		}

		return kernel;

	}

	public int getValue(int rgb) {
		int g = (rgb >> 16) & 0xff;
		return g;

	}

}
