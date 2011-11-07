

/**
 * Represents two consecutive data points in a Bottle.
 */
public class Segment {

	private float radiusA, timeA;
	private float radiusB, timeB;

	/**
	 * Construct a Segment with two data points.
	 * 
	 * @param radiusA Radius of segment A.
	 * @param timeA Time of segment A.
	 * @param radiusB Radius of segment B.
	 * @param timeB Time of segment B.
	 */
	public Segment(float radiusA, float timeA, float radiusB, float timeB) {

		this.radiusA = radiusA;
		this.radiusB = radiusB;
		this.timeA = timeA;
		this.timeB = timeB;
	}

	/**
	 * @return Radius of segment A.
	 */
	public float radiusA() {

		return radiusA;
	}

	/**
	 * @return Time of segment A.
	 */
	public float timeA() {

		return timeA;
	}

	/**
	 * @return Radius of segment B if B exists; Radius of segment A otherwise.
	 */
	public float radiusB() {

		return radiusB;
	}

	/**
	 * @return Time of segment B if B exists; Time of segment A otherwise.
	 */
	public float timeB() {

		return timeB;
	}

	public String toString() {

		return "Segment: [{" + radiusA + ", " + timeA + "}, {" + radiusB + ", " + timeB + "}]";
	}
}
