package edu.uwm.cs351;

import java.awt.Point;
import java.util.ArrayList;

/**
 * A National Park, consisting of a name, location, and 
 * a list of hiking trails. 
 */

public class Park {
	private String name;
	private Point location;
	private ArrayList<Trail> hikingTrails;
	private double minScore, maxScore;
	private double minLength, maxLength;
	
	/**
	 * Constructor for creating a National Park
	 * @param n, the name of the Park
	 * @param loc, the coordinates for the location of the Park
	 * @param t, a list of Trails available at the Park
	 */
	public Park(String n, Point loc, Trail... t) {
		hikingTrails = new ArrayList<Trail>(t.length);
		name = n;
		location = loc;
		for(Trail x : t) {
			hikingTrails.add(x);
			if(x.getDifficulty() < minScore) 
				minScore = x.getDifficulty();
			else if(x.getDifficulty() > maxScore) 
				maxScore = x.getDifficulty();
			if(x.getLength() < minLength) 
				minLength = x.getLength();
			else if(x.getLength() > maxLength) 
				maxLength = x.getLength();
		}
	}
	
	/**
	 * Constructor for a National Park
	 * @param n, the name of the National Park
	 * @param x, longitude coordinate for the Park's location
	 * @param y, latitude coordinate for the Park's location
	 * @param t, list of trails at the Park
	 */
	public Park(String n, int x, int y, Trail... t) {
		hikingTrails = new ArrayList<Trail>(t.length);
		name = n;
		location = new Point(x, y);
		for(Trail s : t)
			hikingTrails.add(s);
	}
	
	public double getDistance(int x, int y) {
		return Math.sqrt((location.x-x)^2 + (location.y-y)^2);
	}

	public double getDistance(Point p) {
		return Math.sqrt((location.x-p.x)^2 + (location.y-p.y)^2);
	}
	
	public double getMaxScore() {
		return maxScore;
	}
	
	public double getMinScore() {
		return minScore;
	}
	
	public double getMaxLength() {
		return maxLength;
	}
	
	public double getMinLength() {
		return minLength;
	}
	
	public String getName() {
		return name;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public ArrayList<Trail> getTrails() {
		return hikingTrails;
	}
	
	public ArrayList<Trail> getTrailsWithMinDifficulty(double s){
		ArrayList<Trail> result = new ArrayList<Trail>();
		for(Trail t : hikingTrails)
			if(t.getDifficulty() >= s)
				result.add(t);
		return result;
	}
	
	public ArrayList<Trail> getTrailsWithMaxDifficulty(double s){
		ArrayList<Trail> result = new ArrayList<Trail>();
		for(Trail t : hikingTrails)
			if(t.getDifficulty() <= s)
				result.add(t);
		return result;
	}
	
	public ArrayList<Trail> getTrailsWithMinLength(double l){
		ArrayList<Trail> result = new ArrayList<Trail>();
		for(Trail t : hikingTrails)
			if(t.getLength() >= l)
				result.add(t);
		return result;
	}
	
	public ArrayList<Trail> getTrailsWithMaxLength(double l){
		ArrayList<Trail> result = new ArrayList<Trail>();
		for(Trail t : hikingTrails)
			if(t.getLength() <= l)
				result.add(t);
		return result;
	}
}
