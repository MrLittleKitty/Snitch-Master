package com.gmail.nuclearcat1337.snitch_master.util;

/**
 * Created by Mr_Little_Kitty on 7/16/2016.
 */
public class GeneralUtils {
	public static double DistanceSquared(int x1, int z1, int x2, int z2) {
		return Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2);
	}

	public static double DistanceSquared(int x1, int z1, int y1, int x2, int z2, int y2) {
		return Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2) + Math.pow(y2 - y1, 2);
	}
}
