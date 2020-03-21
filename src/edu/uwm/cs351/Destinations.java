package edu.uwm.cs351;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A collection of national parks represented by a 
 * doubly linked list. A user may look through the entire collection
 * or a subset of the collection. A cursor is used to iterate. 
 *
 */

public class Destinations {
	private Destination tripStart, tripEnd, tripCursor;
	private Destination head, tail, cursor;
	
	private boolean report(String error) {
		System.out.println("Caught problem: " + error);
		return false;
	}
	
	private boolean wellFormed() {
		boolean foundT = false;
		boolean foundC = false;
		HashSet<Park> s = new HashSet<Park>();
		for(Destination n = head; n != null; n = n.next) {
			if(n == tail) foundT = true;
			if(n.next != null && n.next.prev != n) return report("Improper links");
			if(n.park == null) return report("Null park in list");
			if(!s.contains(n.park)) s.add(n.park);
			else return report("Duplicates in list");
			if(n == cursor) foundC = true;
		}
		if(head == null && tail != null) return report("head is null but tail is not");
		if(head != null && tail == null) return report("head is not null but tail is");
		if(tail != null && !foundT) return report("Tail is not in correct list");
		if(foundT && tail.next != null) return report("Tail is not the last in the list");
		if(cursor != null && !foundC) return report("cursor points to something that is not in list");
		
		return true;
	}
	
	private static class Destination{
		private Park park;
		private Destination next, prev, to;
		
		public Destination(Park p) {
			park = p;
		}
	}

	/**
	 * Create a collection of Parks.
	 * @param p A list of Parks to add to the collection.
	 */
	public Destinations(Park... p) {
		for(Park x : p) 
			add(new Destination(x));
		assert wellFormed(): "Invariant failed at end of constructor";
	}
	
	/**
	 * Makes the first Park in the collection current.
	 */
	public void start() {
		assert wellFormed(): "Invariant failed at beginning of Destinations start";
		cursor = head;
	}
	
	/**
	 * Makes the next Park the current Park.
	 */
	public void next() {
		assert wellFormed(): "Invariant failed at beginning of Destinations next";
		if(!hasCurrent()) throw new IllegalStateException("No current destination!");
		cursor = cursor.next;
		assert wellFormed(): "Invariant failed at end of Destinations next";
	}
	
	/**
	 * Makes the previous Park the current Park.
	 */
	public void previous() {
		assert wellFormed(): "Invariant failed at beginning of Destinations previous";
		if(!hasCurrent()) throw new IllegalStateException("No current destination!");
		cursor = cursor.prev;
		assert wellFormed(): "Invariant failed at end of Destinations previous";
	}
	
	/**
	 * 
	 * @return true if there is a current Park, false otherwise
	 */
	public boolean hasCurrent() {
		assert wellFormed(): "Invariant failed at beginning of Destinations hasCurrent";
		return cursor != null;
	}
	
	/**
	 * 
	 * @return the current Park
	 * @throws IllegalStateException if there is not a current
	 */
	public Park getCurrent() {
		assert wellFormed(): "Invariant failed at beginning of Destinations getCurrent";
		if(!hasCurrent()) throw new IllegalStateException("No current destination!");
		return cursor.park;
	}
	
	private Destination getDestination(Park p) {
		for(Destination d = head; d != null; d = d.next)
			if(d.park == p) return d;
		return null;
	}
	
	/**
	 * Adds a new Park to the end of the collection.
	 * @param p, a Park to be added to the available collection of destinations, must not be null
	 * @return true if added, false if the Park is already in the collection
	 */
	public boolean add(Park p) {
		assert wellFormed(): "Invariant failed at beginning of Destinations add";
		if(p == null) throw new IllegalArgumentException("Cannot add a null");
		boolean result = add(new Destination(p));
		assert wellFormed(): "Invariant failed at end of Destinations add";
		return result;
	}
	
	private boolean add(Destination n) {
		if(getDestination(n.park) != null) return false;
		if(head == null) head = tail = n;
		else {
			n.prev = tail;
			tail = tail.next = n;
		}
		return true;
	}
	
	/**
	 * Removes the current Park from the collection of available destinations, and from any trips that contain it.
	 * The next Park, if any, becomes the current Park.
	 */
	public void remove() {
		assert wellFormed(): "Invariant failed at beginning of Destinations remove";
		if(!hasCurrent()) throw new IllegalStateException("No current destination!");
		createTrip().remove(cursor.park);
		if(cursor == head) {
			head = head.next;
			if(head == null) tail = null;
			else head.prev = null;
		}
		else if(cursor == tail) {
			tail = tail.prev;
			tail.next = null;
		}
		else {
			/*
			  Given bug:
			  
			  cursor.next.prev = cursor.next;
			  cursor.prev.next = cursor.prev;
			 */
			
			//Solution, next 2 lines:
			cursor.next.prev = cursor.prev;
			cursor.prev.next = cursor.next;
		}
		cursor = cursor.next;
		assert wellFormed(): "Invariant failed at end of Destinations remove";
	}
	
	@Override
	public String toString() {
		assert wellFormed(): "Invariant failed at beginning of Destinations toString";
		StringBuilder sb = new StringBuilder();
		for(Destination d = head; d != null; d = d.next) {
			sb.append(d.park.getName());
			if(d.next != null) sb.append(", ");
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param miles, the maximum distance from the given location
	 * @param loc, a beginning location to measure distance from
	 * @return a list of available Parks that are within miles distance from the starting loc
	 */
	public ArrayList<Park> withinDistance(int miles, Point loc) {
		assert wellFormed(): "Invariant failed at beginning of Destinations withinDistance";
		ArrayList<Park> result = new ArrayList<Park>();
		
		for(Destination n = head; n != null; n = n.next) {
			if(n.park.getDistance(loc) < miles)
				result.add(n.park);
		}
		return result;
	}
	
	/**
	 * 
	 * @param score, a measurement of the minimum difficulty of a hiking trail
	 * @return a list of available Parks with a minimum difficulty of the given score
	 */
	public ArrayList<Park> withMinDifficultyTrails(double score) {
		assert wellFormed(): "Invariant failed at beginning of Destinations withMinDifficultyTrails";
		ArrayList<Park> result = new ArrayList<Park>();
		
		for(Destination n = head; n != null; n = n.next) {
			if(n.park.getMinScore() > score)
				result.add(n.park);
		}
		return result;
	}
	
	/**
	 * 
	 * @param score, a measurement of the maximum difficulty of a hiking trail
	 * @return a list of available Parks with a maximum difficulty of the given score
	 */
	public ArrayList<Park> withMaxDifficultyTrails(double score) {
		assert wellFormed(): "Invariant failed at beginning of Destinations withMaxDifficultyTrails";
		ArrayList<Park> result = new ArrayList<Park>();
		
		for(Destination n = head; n != null; n = n.next) {
			if(n.park.getMaxScore() < score)
				result.add(n.park);
		}
		return result;
	}
	
	/**
	 * 
	 * @param l, a minimum length in miles of a hiking trail
	 * @return a list of available Parks with trails longer than or equal to l in length
	 */
	public ArrayList<Park> withMinLengthTrails(double l) {
		assert wellFormed(): "Invariant failed at beginning of Destinations withMinLengthTrails";
		ArrayList<Park> result = new ArrayList<Park>();
		
		for(Destination n = head; n != null; n = n.next) {
			if(n.park.getMinLength() > l)
				result.add(n.park);
		}
		return result;
	}
	
	/**
	 * 
	 * @param l, a maximum length in miles of a hiking trail
	 * @return a list of available Parks with trails less than or equal to l in length
	 */
	public ArrayList<Park> withMaxLengthTrails(double l) {
		assert wellFormed(): "Invariant failed at beginning of Destinations withMaxLengthTrails";
		ArrayList<Park> result = new ArrayList<Park>();
		
		for(Destination n = head; n != null; n = n.next) {
			if(n.park.getMaxLength() < l)
				result.add(n.park);
		}
		return result;
	}
	
	public Trip createTrip() {
		assert wellFormed(): "Invariant failed at beginning of Destinations createTrip";
		return new Trip();
	}
	
	/**
	 * A Trip is a sequence of Parks to be visited, chosen from the collection of Destinations. A user can iterate
	 * through the Trip, add Parks to and remove Parks from the Trip.
	 *
	 */
	public class Trip {
		
		private boolean report(String error) {
			System.out.println("Caught problem: " + error);
			return false;
		}
		
		private boolean wellFormed() {
			assert Destinations.this.wellFormed();
			
			Destination fast = head;
			Destination slow = head;
			while(fast != null && fast.to != null) {
				slow = slow.to;
				fast = fast.to.to;
				if(slow == fast) return report("There is a cycle in the list!");
			}
			
			boolean foundT = false;
			boolean foundC = false;
			for(Destination n = tripStart; n != null; n = n.to) {
				if(n == tripCursor) foundC = true;
				if(n == tripEnd) foundT = true;
			}
			
			if(tripStart == null && tripEnd != null) return report("start is null but end is not");
			if(tripStart != null && tripEnd == null) return report("start is not null but end is");
			if(tripEnd != null && !foundT) return report("end is in the wrong list");
			if(tripCursor != null && !foundC) return report("Cursor is in the wrong list");
			if(tripEnd != null && tripEnd.to != null) return report("TripEnd is not the last in the list");
			
			return true;
		}
		
		/**
		 * Makes the first Park in the Trip the current Park.
		 */
		public void start() {
			assert wellFormed(): "Invariant failed at beginning of Trip start";
			tripCursor = tripStart;
		}
		
		/**
		 * The next Park is the new current Park.
		 * @throws IllegalStateException if hasCurrent is false.
		 */
		public void next() {
			assert wellFormed(): "Invariant failed at beginning of Trip next";
			if(!hasCurrent()) throw new IllegalStateException("No current park!");
			tripCursor = tripCursor.to;
			assert wellFormed(): "Invariant failed at end of Trip next";
		}
		
		/**
		 * 
		 * @return true if there is a current Park.
		 */
		public boolean hasCurrent() {
			assert wellFormed(): "Invariant failed at beginning of Trip hasCurrent";
			return tripCursor != null;
		}
		
		/**
		 * @throws IllegalStateExcecption if there is no current Park
		 * @return the current Park
		 */
		public Park getCurrent() {
			assert wellFormed(): "Invariant failed at beginning of Trip getCurrent";
			if(!hasCurrent()) throw new IllegalStateException("No current park!");
			if(tripCursor == null) return null;
			return tripCursor.park;
		}
		
		/**
		 * Add the new Park after the current Park, if it exists. Otherwise, the new Park is added at the end of the Trip.
		 * @param p new Park to be added to the Trip, must not be null
		 * @return true if the Park is added, false if it is not an available destination
		 */
		public boolean add(Park p) {
			assert wellFormed(): "Invariant failed at beginning of Trip add";
			Destination d = getDestination(p);
			if(d == null) return false;
			
			if(tripCursor == null) {
				if(tripStart == null) tripStart = tripEnd = d;
				else tripEnd = tripEnd.to = d;
			}
			else {
				d.to = tripCursor.to;
				tripCursor = tripCursor.to = d;
				if(tripEnd.to != null) tripEnd = tripEnd.to;
			}

			assert wellFormed(): "Invariant failed at end of Trip add";
			return true;
		}
		
		/**
		 * Remove the current Park from the Trip. The next Park, if it exists, 
		 * is the new current Park.
		 */
		public void remove() {
			assert wellFormed(): "Invariant failed at beginning of Trip remove";
			if(!hasCurrent()) throw new IllegalStateException("No current park!");
			if(tripCursor == tripStart) {
				tripStart = tripStart.to;
				if(tripStart == null) tripEnd = null;
			}
			else {
				Destination d;
				for(d = tripStart; d.to != tripCursor; d = d.to);
				
				/*
				 Given bug:
				
				 d.next = d.next.next;
				*/
				
				//Solution, next line:
				d.to = d.to.to;
				
				if(d.to == null) tripEnd = d;
			}
			
			tripCursor = tripCursor.to;

			assert wellFormed(): "Invariant failed at end of Trip remove";
		}
		
		private void remove(Park p) {
			Destination c = getDestination(p);
			Destination d = tripStart;
			if(c == d) {
				tripStart = tripStart.to;
				if(tripStart == null) {
					tripEnd = tripCursor = null;
				}
			}
			else {
				while(d != null && d.to != c)
					d = d.to;
				if(d != null) {
					if(tripCursor == c) tripCursor = tripCursor.to;
					d.to = d.to.to;
					if(d.to == null) tripEnd = d;
				}
			}
		}
		
		@Override
		public String toString() {
			assert wellFormed(): "Invariant failed at beginning of Trip toString";
			StringBuilder sb = new StringBuilder();
			for(Destination d = tripStart; d != null; d = d.to) {
				sb.append(d.park.getName());
				if(d.to != null) sb.append(", ");
			}
			return sb.toString();
		}
	}
	
	
}
