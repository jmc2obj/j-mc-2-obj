package org.jmc.util;

import java.awt.Point;

public class Hilbert {
	// Adapted from https://en.wikipedia.org/wiki/Hilbert_curve#Applications_and_mapping_algorithms
	//convert (x,y) to d
	public static long pointToIndex (int size, Point pt) {
		Point p = new Point(pt);// copy to not destroy the passed point
	    int rx, ry, level;
		long index = 0;
	    for (level=size/2; level>0; level/=2) {
	        rx = (p.x & level) > 0 ?1:0;
	        ry = (p.y & level) > 0 ?1:0;
	        index += level * level * ((3 * rx) ^ ry);
	        rot(size, p, rx, ry);
	    }
	    return index;
	}

	//convert d to (x,y)
	public static Point indexToPoint(int size, int index) {
		Point p = new Point();
	    int rx, ry, level, t=index;
	    for (level=1; level<size; level*=2) {
	        rx = 1 & (t/2);
	        ry = 1 & (t ^ rx);
	        rot(level, p, rx, ry);
	        p.x += level * rx;
	        p.y += level * ry;
	        t /= 4;
	    }
	    return p;
	}

	//rotate/flip a quadrant appropriately
	private static void rot(int size, Point p, int rx, int ry) {
	    if (ry == 0) {
	        if (rx == 1) {
	            p.x = size-1 - p.x;
	            p.y = size-1 - p.y;
	        }

	        //Swap x and y
	        int t  = p.x;
	        p.x = p.y;
	        p.y = t;
	    }
	}
}
