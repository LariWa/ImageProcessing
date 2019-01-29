// BV Ue2 WS2018/19 Vorgabe
//
// Copyright (C) 2017 by Klaus Jung
// All rights reserved.
// Date: 2017-07-15

package bv_ws1819;


public class GeometricTransform {

	public enum InterpolationType { 
		NEAREST("Nearest Neighbour"), 
		BILINEAR("Bilinear");
		
		private final String name;       
	    private InterpolationType(String s) { name = s; }
	    public String toString() { return this.name; }
	};
	
	public void perspective(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion, InterpolationType interpolation) {
		switch(interpolation) {
		case NEAREST:
			perspectiveNearestNeighbour(src, dst, angle, perspectiveDistortion);
			break;
		case BILINEAR:
			perspectiveBilinear(src, dst, angle, perspectiveDistortion);
			break;
		default:
			break;	
		}
		
	}

	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 */
	public void perspectiveNearestNeighbour(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using nearest neighbour image rendering
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radians
		double s = perspectiveDistortion;
		
		for (int yd = 0; yd < dst.height; yd++) {
			for (int xd = 0; xd < dst.width; xd++) {
				int xdneu = xd - dst.width/2;
				int ydneu = yd - dst.height/2;
				double xst = xdneu/(Math.cos(Math.toRadians(angle)) - xdneu * s * Math.sin(Math.toRadians(angle)));
				double yst = ydneu * (s * Math.sin(Math.toRadians(angle)) * xst + 1);
				int xs = (int) Math.round(xst + src.width/2);
				int ys = (int) Math.round(yst + src.height/2);
				
				int posd = yd * dst.width + xd;
				int poss = ys * src.width + xs;
				
				if ((xs < 0) || (xs > src.width-1) || (ys < 0) || (ys > src.height-1))  {
					dst.argb[posd] = 0xFFFFFFFF;
				} else {
					dst.argb[posd] = src.argb[poss];
				}
				
			}
		}
		
	}


	/**
	 * @param src source image
	 * @param dst destination Image
	 * @param angle rotation angle in degrees
	 * @param perspectiveDistortion amount of the perspective distortion 
	 */
	public void perspectiveBilinear(RasterImage src, RasterImage dst, double angle, double perspectiveDistortion) {
		// TODO: implement the geometric transformation using bilinear interpolation
		
		// NOTE: angle contains the angle in degrees, whereas Math trigonometric functions need the angle in radians
		
		double s = perspectiveDistortion;
		
		for (int yd = 0; yd < dst.height; yd++) {
			for (int xd = 0; xd < dst.width; xd++) {
				int xdneu = xd - dst.width/2;
				int ydneu = yd - dst.height/2;
				double xst = xdneu/(Math.cos(Math.toRadians(angle)) - xdneu * s * Math.sin(Math.toRadians(angle)));
				double yst = ydneu * (s * Math.sin(Math.toRadians(angle)) * xst + 1);
				double xs = xst + src.width/2;
				double ys = yst + src.height/2;
				
				int xsi = (int) xs; // double 3,7 -> int 3
				double h = xs - xsi; // 3,7 - 3 -> 0,7
				
				int ysi = (int) ys;
				double v = ys - ysi;
				
				int posd = yd * dst.width + xd;
				int poss = ysi * src.width + xsi;
					
				int A, B, C, D;
				
				if ((xs < 0) || (xs > src.width-1) || (ys < 0) || (ys > src.height-1))  {
					A = 0xFFFFFFFF;
				} else {
					A = src.argb[poss];
				}
				
					if ((xsi < src.width - 1) && (xsi >= 0) && (ysi >= 0) && (ysi < src.height - 1)) {
						B = src.argb[poss + 1];
					}
					else {
						B = A;
					}
					
					if ((ysi < src.height - 1) && (ysi > 0) && (xsi < src.width - 1) && (xsi >= 0)) {
						C = src.argb[(ysi + 1) * src.width + xsi];
					}
					else {
						C = A;
					}
					
					if ((xs > 0) && (xs < src.width-1) && (ys > 0) && (ys < src.height-1)) {
						D = src.argb[(ysi + 1) * src.width + xsi + 1];
					}
					else if ((xsi >= src.width - 1) && (ysi >= src.height - 1)) {
						D = A;
					}
					else if ((xsi >= src.width - 1) && (ysi >= 0) && (ysi <= src.height - 1)) {
						D = C;
					}
					else if ((xsi >= 0) && (xsi <= src.width) && (ysi >= src.height - 1)) {
						D = B;
					} else {
						D = A;
					}

				int rA = (A >> 16) & 0xff;
				int gA = (A >> 8) & 0xff;
				int bA = A & 0xff;
				int rB = (B >> 16) & 0xff;
				int gB = (B >> 8) & 0xff;
				int bB = B & 0xff;
				int rC = (B >> 16) & 0xff;
				int gC = (B >> 8) & 0xff;
				int bC = C & 0xff;
				int rD = (C >> 16) & 0xff;
				int gD = (D >> 8) & 0xff;
				int bD = D & 0xff;

				int rn = (int) Math
						.round(rA * (1 - h) * (1 - v) + rB * h * (1 - v) + rC * (1 - h) * v + rD * h * v);
				int gn = (int) Math
						.round(gA * (1 - h) * (1 - v) + gB * h * (1 - v) + gC * (1 - h) * v + gD * h * v);
				int bn = (int) Math
						.round(bA * (1 - h) * (1 - v) + bB * h * (1 - v) + bC * (1 - h) * v + bD * h * v);

				if (rn > 255) rn = 255;
				if (gn > 255) gn = 255;
				if (bn > 255) bn = 255;
				if (rn > 255) rn = 255;
				if (gn > 255) gn = 255;
				if (bn > 255) bn = 255;
				if (rn < 0)   rn = 0;
				if (gn < 0)   gn = 0;
				if (bn < 0)   bn = 0;

				dst.argb[posd] = (0xff << 24) | (rn << 16) | (gn << 8) | (bn);
		
			}
		}
	}
}
