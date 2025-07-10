import java.awt.*;
import java.awt.image.*;
import java.sql.SQLOutput;
import java.util.*;

/**
 @author Julia Allos
CS10- Problem Set 1
4/8/24
 */

/**
 * Code provided for PS-1
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Dartmouth CS 10, Winter 2024
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering
	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions; 			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}


	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		//initializing regions
		regions = new ArrayList<>();
		//creating other image and setting it to black
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int y = 0; y < getImage().getHeight(); y++) {
			for(int x = 0; x < getImage().getWidth(); x++) {
				visited.setRGB(x, y, 0); }
		}
		ArrayList<Point> needsVisiting = new ArrayList<>();
		for(int y = 0; y < image.getHeight(); y++) {
			for(int x = 0; x < image.getWidth(); x++) {
				if(visited.getRGB(x,y)==1){ continue; }
				Color colorPixel = new Color(image.getRGB(x,y));
				if(colorMatch(colorPixel, targetColor)) {
					ArrayList<Point> newRegion = new ArrayList<>();
					needsVisiting.add(new Point(x,y));
					newRegion.add(new Point(x,y));
					visited.setRGB(x,y,1);
					while (!needsVisiting.isEmpty()) {
						for (int ny = Math.max(0, (int)needsVisiting.get(0).getY() - 1); ny < Math.min(image.getHeight(), (int)needsVisiting.get(0).getY() + 1 + 1); ny++) {
							for (int nx = Math.max(0, (int)needsVisiting.get(0).getX() - 1); nx < Math.min(image.getWidth(), (int)needsVisiting.get(0).getX() + 1 + 1); nx++) {
								Color neighborColor = new Color(image.getRGB(nx, ny));
								if(visited.getRGB(nx,ny) == 0) {
									if (colorMatch(neighborColor, targetColor)) {
										needsVisiting.add(new Point(nx, ny));
										newRegion.add(new Point(nx,ny));
									}
									visited.setRGB(nx,ny,1);
								}
							}
						}
						needsVisiting.remove(0);
					}
					if(newRegion.size() >= minRegion) { regions.add(newRegion); }
				}
				else { visited.setRGB(x,y,1); }
				}
			}
		}



	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	protected static boolean colorMatch(Color c1, Color c2) {
		int col1red = c1.getRed();
		int col1green = c1.getGreen();
		int col1blue = c1.getBlue();
		int col2red = c2.getRed();
		int col2green = c2.getGreen();
		int col2blue = c2.getBlue();

		int totalRed = Math.abs(col1red - col2red);
		int totalBlue = Math.abs(col1blue - col2blue);
		int totalGreen = Math.abs(col1green - col2green);

		if(totalRed<maxColorDiff && totalGreen < maxColorDiff && totalBlue<maxColorDiff) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		if(regions.isEmpty()) { return null; }
		int max = minRegion;
		int indexOfMax = 0;
		for(int i=0; i < regions.size(); i++) {
			if(regions.get(i).size() >max) {
				max = regions.get(0).size();
				indexOfMax = i;
			}
		}
		return regions.get(indexOfMax);
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for(int i=0; i < regions.size(); i++) {
			double red= Math.random()*256;
			double green= Math.random()*256;
			double blue= Math.random()*256;
			Color regionColor = new Color((int)red,(int)green,(int)blue);
			ArrayList<Point> currentList= regions.get(i);
			for(int j=0; j<regions.get(i).size(); j++) {
				Point currentPoint = currentList.get(j);
				int x = (int)currentPoint.getX();
				int y = (int) currentPoint.getY();
				recoloredImage.setRGB(x,y, regionColor.getRGB());
			}
		}

	}
}
