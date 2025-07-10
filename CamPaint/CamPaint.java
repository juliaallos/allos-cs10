import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.*;

/**
 * @author Julia Allos
 * CS10- Problem Set 1
 * April 8, 2024
 */

/**
 * Code provided for PS-1
 * Webcam-based drawing
 * Dartmouth CS 10, Winter 2024
 */
public class CamPaint extends VideoGUI {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece


	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * VideoGUI method, here drawing one of live webcam, recolored image, or painting,
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void handleImage() {
		finder = new RegionFinder();
		finder.setImage(image);
		if(targetColor != null) { //safety check
			finder.findRegions(targetColor);
			ArrayList<Point> paintbrush = finder.largestRegion();
			for(Point b: paintbrush){
				painting.setRGB(b.x, b.y, paintColor.getRGB());
			}
		}
		//showing painting
		if(displayMode == 'p') {
			setImage1(painting); }
		//showing webcam
		else if(displayMode == 'w') {
			setImage1(image); }
		//camera will detect regions
		else if(displayMode == 'r') {
			if(targetColor!=null) { //safety check
				Color newColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
				ArrayList<Point> paintbrush = finder.largestRegion();
				for (Point b : paintbrush) {
					image.setRGB(b.x, b.y, newColor.getRGB());
				}
			}
			setImage1(image);
		}
	}


	/**
	 * Overrides the Webcam method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		targetColor = new Color(image.getRGB(x,y));
	}

	/**
	 * Webcam method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			ImageIOLibrary.saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			ImageIOLibrary.saveImage(painting, "pictures/painting.png", "png");
		}
		//setting colors
		else if (k == 'm') { paintColor = Color.magenta; }
		else if (k == 'l') { paintColor = Color.pink; }
		else if (k == 'g') { paintColor = Color.green; }
		else if (k == 'b') { paintColor = Color.blue; }
		else {
			System.out.println("unexpected key "+k);
		}
	}
	//main method
	public static void main(String[] args) {
		new CamPaint();
	}
}
