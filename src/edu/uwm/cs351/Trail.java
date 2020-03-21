package edu.uwm.cs351;

/**
 * A trail with a name, difficulty score, and length in miles.
 *
 */
public class Trail {
	private static String name;
	private static double score;
	private static double length;

	public Trail(String n, double s, double l) {
		name = n;
		score = s;
		length = l;
	}
	
	public String getTrailName() {
		return name;
	}
	
	public double getDifficulty() {
		return score;
	}
	
	public double getLength() {
		return length;
	}
}
