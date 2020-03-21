import edu.uwm.cs351.Destinations;
import edu.uwm.cs351.Destinations.Trip;
import edu.uwm.cs351.Park;
import edu.uwm.cs351.Trail;
import junit.framework.TestCase;

public class TestDestinations extends TestCase {
	
	protected void assertException(Class<? extends Throwable> c, Runnable r) {
		try {
			r.run();
			assertFalse("Exception should have been thrown",true);
		} catch (RuntimeException ex) {
			assertTrue("should throw exception of " + c + ", not of " + ex.getClass(), c.isInstance(ex));
		}
	}
	
	private Destinations parks;
	private Trip roadTrip;
	private Park p1, p2, p3, p4, p5;
	
	public void setUp() {
		p1 = new Park("Zion National Park", 37, 113, new Trail("Angel's Landing", 7, 5), 
				new Trail("The Watchman", 5, 3.1), new Trail("Emerald Pools Overlook", 3, 2.9));
		p2 = new Park("Grand Canyon National Park", 36, 112, new Trail("Rim-to-Rim", 9, 21.6),
				new Trail("Bright Angel Point Trail", 3, 0.9), new Trail("South Kaibab to Cedar Ridge", 5, 3.1));
		p3 = new Park("Bryce Canyon National Park", 38, 112, new Trail("Navajo Loop and Queen's Garden", 5, 2.6), 
				new Trail("Peekaboo Loop", 4, 5.2), new Trail("Under the Rim", 9, 22.4));
		p4 = new Park("Yellowstone National Park", 44, 111, new Trail("Mystic Falls, Fairy Creek, and Little Firehole Loop", 4, 3.5),
				new Trail("Grand Prismatic Hot Spring", 2, 1.6));
		p5 = new Park("Yosemite National Park", 38, 120, new Trail("Upper Yosemite Falls", 8, 7.2), 
				new Trail("Vernal Falls", 6, 3.47), new Trail("Clouds Rest", 9, 12.3));

		parks = new Destinations(p1, p2, p3);
	}
	
	public void test0() {
		assertFalse(parks.hasCurrent());
		assertException(IllegalStateException.class, () -> parks.next());
		assertException(IllegalStateException.class, () -> parks.previous());
		assertException(IllegalStateException.class, () -> parks.getCurrent());
		parks.start();
		assertTrue(parks.hasCurrent());
		parks.next();
		assertSame(p2, parks.getCurrent());
		parks.previous();
		assertSame(p1, parks.getCurrent());
		parks.next();
		parks.next();
		assertSame(p3, parks.getCurrent());
		parks.next();
		assertFalse(parks.hasCurrent());
		assertException(IllegalStateException.class, () -> parks.remove());
	}
	
	public void test1() {
		assertFalse(parks.hasCurrent());
		parks.add(p4);
		parks.start();
		assertSame(p1, parks.getCurrent());
		parks.next();
		assertSame(p2, parks.getCurrent());
		parks.next();
		assertSame(p3, parks.getCurrent());
		parks.next();
		assertSame(p4, parks.getCurrent());
		parks.add(p5);
		parks.next();
		assertSame(p5, parks.getCurrent());
	}
	
	public void test2() {
		assertTrue(parks.add(p4));
		assertEquals("Zion National Park, Grand Canyon National Park, Bryce Canyon National Park, Yellowstone National Park", parks.toString());
		assertTrue(parks.add(p5));
		assertEquals("Zion National Park, Grand Canyon National Park, Bryce Canyon National Park, Yellowstone National Park, Yosemite National Park", parks.toString());
		assertFalse(parks.add(p2));
		assertEquals("Zion National Park, Grand Canyon National Park, Bryce Canyon National Park, Yellowstone National Park, Yosemite National Park", parks.toString());
		assertException(IllegalArgumentException.class, () -> parks.add(null));
	}
	
	public void test3() {
		assertEquals("Zion National Park, Grand Canyon National Park, Bryce Canyon National Park", parks.toString());
		parks.start();
		parks.next();
		parks.remove();
		assertEquals("Zion National Park, Bryce Canyon National Park", parks.toString());
		assertSame(p3, parks.getCurrent());
		parks.remove();
		assertFalse(parks.hasCurrent());
		assertEquals("Zion National Park", parks.toString());
		parks.start();
		parks.remove();
		assertFalse(parks.hasCurrent());
		assertEquals("", parks.toString());
	}
	
	public void test4() {
		roadTrip = parks.createTrip();
		assertFalse(parks.hasCurrent());
		assertFalse(roadTrip.hasCurrent());
		parks.start();
		assertTrue(parks.hasCurrent());
		assertFalse(roadTrip.hasCurrent());
		roadTrip.start();
		assertFalse(roadTrip.hasCurrent());
	}
	
	public void test5() {
		roadTrip = parks.createTrip();
		parks.start();
		parks.next();
		assertTrue(roadTrip.add(parks.getCurrent()));
		roadTrip.start();
		assertTrue(roadTrip.hasCurrent());
		assertSame(p2, roadTrip.getCurrent());
		roadTrip.next();
		assertFalse(roadTrip.hasCurrent());
	}
	
	public void test6() {
		roadTrip = parks.createTrip();
		assertFalse(roadTrip.add(p4));
		parks.add(p4);
		assertTrue(roadTrip.add(p4));
		assertTrue(roadTrip.add(p1));
	}
	
	public void test7() {
		roadTrip = parks.createTrip();
		roadTrip.add(p2);
		roadTrip.add(p3);
		roadTrip.add(p1);
		assertEquals("Grand Canyon National Park, Bryce Canyon National Park, Zion National Park", roadTrip.toString());
		assertEquals("Zion National Park, Grand Canyon National Park, Bryce Canyon National Park", parks.toString());
		roadTrip.start();
		roadTrip.remove();
		assertEquals("Bryce Canyon National Park, Zion National Park", roadTrip.toString());
		assertEquals("Zion National Park, Grand Canyon National Park, Bryce Canyon National Park", parks.toString());
		parks.start();
		parks.remove();
		assertEquals("Bryce Canyon National Park", roadTrip.toString());
		assertEquals("Grand Canyon National Park, Bryce Canyon National Park", parks.toString());
		roadTrip.next();
		assertException(IllegalStateException.class, () -> roadTrip.remove());
	}
	
	public void test8() {
		roadTrip = parks.createTrip();
		roadTrip.add(p2);
		roadTrip.add(p3);
		roadTrip.add(p1);
		assertEquals("Grand Canyon National Park, Bryce Canyon National Park, Zion National Park", roadTrip.toString());
		assertEquals("Zion National Park, Grand Canyon National Park, Bryce Canyon National Park", parks.toString());
		roadTrip.start();
		roadTrip.next();
		roadTrip.remove();
		assertEquals("Grand Canyon National Park, Zion National Park", roadTrip.toString());
		assertEquals("Zion National Park, Grand Canyon National Park, Bryce Canyon National Park", parks.toString());
		parks.start();
		parks.remove();
		assertEquals("Grand Canyon National Park", roadTrip.toString());
		assertEquals("Grand Canyon National Park, Bryce Canyon National Park", parks.toString());
		assertException(IllegalStateException.class, () -> roadTrip.next());
	}
}
