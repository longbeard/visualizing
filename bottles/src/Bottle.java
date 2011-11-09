

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Represents a collection of segments that stand for an indicator over time.
 */
public class Bottle implements Iterable<Segment> {

	// number of sides to be rendered
	public static final int SIDES = 500;

	// view
	private PApplet parent;

	// segments
	private ArrayList<Segment> segments;
	private boolean appendSegment = false;
	private float pRadius, pTime;

	// state
	private int red, green, blue, alpha;
	private float x, y, z;

	/**
	 * Construct a Bottle with an ArrayList of Segments.
	 * 
	 * @param segments The ArrayList of Segments that compose this Bottle.
	 */
	public Bottle(PApplet parent, ArrayList<Segment> segments, float x, float y, float z, int red, int green, int blue, int alpha) {

		this.parent = parent;
		this.segments = segments;
		this.x = x;
		this.y = y;
		this.z = z;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	/**
	 * Construct the Bottle representing indicator and country over time.
	 * 
	 * @param indicators The indicator list requested.
	 * @param country The country requested.
	 */
	public Bottle(PApplet parent, ArrayList<String> indicators, String country, float x, float y, float z, int red, int green, int blue, int alpha) {

		this.parent = parent;
		this.x = x;
		this.y = y;
		this.z = z;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		segments = new ArrayList<Segment>();

		HashMap<String, Float> byYear;
		float yearAverage, indicatorTotal;
		String indicator;
		Iterator<String> indicatorIterator;

		for (int year = 1990; year <= 2010; year++) {
			indicatorIterator = indicators.iterator();
			indicatorTotal = 0;
			while (indicatorIterator.hasNext()) {
				indicator = indicatorIterator.next();
				byYear = Data.normalizedDatabase().get(indicator).get(country);
				if (byYear.size() == 0) {
					indicatorIterator.remove();
					continue;
				}
				indicatorTotal += byYear.get(year + "");
			}
			yearAverage = indicatorTotal / indicators.size();
			appendSegment(yearAverage, year);
		}
	}

	/**
	 * Appends a segment to current segments
	 * 
	 * @param radius Radius of the new data point.
	 * @param time Time of the new data point.
	 */
	private void appendSegment(float radius, float time) {

		if (appendSegment) {
			segments.add(new Segment(pRadius, pTime, radius, time));
			pRadius = radius;
			pTime = time;
		} else {
			pRadius = radius;
			pTime = time;
			appendSegment = true;
		}
	}

	@Override
	public Iterator<Segment> iterator() {

		return segments.iterator();
	}

	/**
	 * Draw this bottle.
	 */
	public void draw() {

		parent.pushMatrix();

		parent.fill(red, green, blue, alpha);
		parent.translate(x, y, z);

		float angle = 0;
		float angleIncrement = PConstants.TWO_PI / SIDES;

		for (Segment segment : this) {
			parent.beginShape(PConstants.QUAD_STRIP);
			for (int sideIndex = 0; sideIndex <= SIDES; sideIndex++) {
				parent.vertex(segment.radiusA() * PApplet.cos(angle), 50f * (segment.timeA() - 1990), segment.radiusA() * PApplet.sin(angle));
				parent.vertex(segment.radiusB() * PApplet.cos(angle), 50f * (segment.timeB() - 1990), segment.radiusB() * PApplet.sin(angle));
				angle += angleIncrement;
			}
			parent.endShape();
		}

		parent.popMatrix();
	}

	/**
	 * Set the color of this bottle
	 * 
	 * @param red [0-255]
	 * @param green [0-255]
	 * @param blue [0-255]
	 * @param alpha [0-255]
	 */
	public void setColor(int red, int green, int blue, int alpha) {

		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
}