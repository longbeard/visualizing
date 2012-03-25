

import java.util.ArrayList;
import processing.core.*;

/**
 * PApplet that expresses the data set of "How Might We Grow?" (visualizing 2011).
 */
public class View extends PApplet {

	private static final int SIZE_X = 1200;
	private static final int SIZE_Y = 800;
	private static final int BOTTLES_BG = 255;
	private static final int CONTROL_BG = 255;
	private static final float CONTROL_WIDTH = 240f;
	private static final float FADE_WIDTH = 20f;

	private float rotationX, rotationY, scale, translationX, translationY;
	private ArrayList<Bottle> bottles;
	private Bottle timeBottle;
	private PImage legend;

	/**
	 * Initialization function.
	 */
	public void setup() {

		size(SIZE_X, SIZE_Y, P3D);
		resetView();

		addMouseWheelListener(new java.awt.event.MouseWheelListener() {
			public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {

				mouseWheel(evt.getWheelRotation());
			}
		});

		ArrayList<Segment> timeSegments = new ArrayList<Segment>();
		timeSegments.add(new Segment(5, 1990, 5, 2010));
		timeSegments.add(new Segment(10, 2010, 0, 2011));

		timeBottle = new Bottle(this, timeSegments, 0, 0, 0, 0, 0, 0, 200);

		bottles = new ArrayList<Bottle>();

		ArrayList<String> economicAttributes = new ArrayList<String>();
		economicAttributes.add("gdp");
		economicAttributes.add("fdi");
		economicAttributes.add("oda");

		ArrayList<String> socialAttributes = new ArrayList<String>();
		socialAttributes.add("cellularPer100");
		socialAttributes.add("healthExpendeture");
		socialAttributes.add("internetUsersPer100");
		// socialAttributes.add("popThousands");
		socialAttributes.add("telephoneSubscription");
		socialAttributes.add("urbanPop");
		// socialAttributes.add("waterAccess");

		ArrayList<String> environmentalAttributes = new ArrayList<String>();
		environmentalAttributes.add("co2");

		bottles.add(new Bottle(this, economicAttributes, "United States", 0f, 0f, 0f, 255, 0, 0, 30));
		bottles.add(new Bottle(this, socialAttributes, "United States", 0f, 0f, 0f, 0, 255, 0, 30));
		bottles.add(new Bottle(this, environmentalAttributes, "United States", 0f, 0f, 0f, 0, 0, 255, 30));

		legend = loadImage("images/legend.png");
	}

	/**
	 * Render everything for the current frame.
	 */
	public void draw() {

		ortho(-width / 2, width / 2, -height / 2, height / 2, -10, 10);
		lights();

		checkPanOrTilt();

		drawBottles();
		drawControlPanel();
		drawFadeBorder();
	}

	/**
	 * Render the bottles.
	 */
	void drawBottles() {

		pushMatrix();

		background(BOTTLES_BG);
		noStroke();
		translate(translationX, translationY);
		rotateX(rotationX);
		rotateZ(rotationY);
		scale(scale);

		timeBottle.draw();
		for (Bottle bottle : bottles) {
			bottle.draw();
		}

		popMatrix();
	}

	/**
	 * Render the control panel.
	 */
	void drawControlPanel() {

		pushMatrix();

		fill(CONTROL_BG);
		rect(0, 0, CONTROL_WIDTH, height);
		image(legend, 0, 600);

		popMatrix();
	}

	/**
	 * Render the fade border.
	 */
	void drawFadeBorder() {

		pushMatrix();

		translate(CONTROL_WIDTH, 0);
		for (int x = 0; x <= FADE_WIDTH; x++) {
			stroke(CONTROL_BG, map((float)x/FADE_WIDTH, 0, FADE_WIDTH, 1, 0));
			line(x, 0, x, height);
		}

		popMatrix();
	}

	/**
	 * Modify rotation or translation based on current mouse state.
	 */
	void checkPanOrTilt() {

		if (mousePressed) {
			switch (mouseButton) {
			case LEFT:
				rotationX += PI * ((float) (mouseY - pmouseY) / height);
				rotationY += PI * ((float) (mouseX - pmouseX) / width);
				break;
			case RIGHT:
				translationX += mouseX - pmouseX;
				translationY += mouseY - pmouseY;
			}
		}
	}

	/**
	 * Event handler for mouse scrolls.
	 * 
	 * @param delta change in scroll (clicks)
	 */
	void mouseWheel(int delta) {

		scale += delta * 0.01f;
	}

	@Override
	public void keyReleased() {

		if (key == ' ') {
			resetView();
		}
	}

	/**
	 * Reset the camera values.
	 */
	private void resetView() {

		rotationX = PI / 3;
		rotationY = PI / 3;
		scale = 1;
		translationX = 520;
		translationY = height/2;
	}

	/**
	 * Included as a utility when run as a local application.
	 */
	public static void main(String[] args) {

		PApplet.main(new String[] { "--present", "visualizing.View" });
	}
}