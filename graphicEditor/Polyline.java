import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 */
public class Polyline implements Shape {
	private List<Point> points;
	private Color color;

	public Polyline(int x, int y, Color color) {
		points = new ArrayList<>();
		points.add(new Point(x, y));
		this.color = color;
	}

	public Polyline(List<Point> points, Color color) {
		this.points = points;
		this.color = color;
	}

	public void addPoints(int x, int y) {
		points.add(new Point(x, y));
	}
	@Override
	public void moveBy(int dx, int dy) {
		for(Point p: points) {
			p.setLocation(p.getX() + dx, p.getY() + dy);
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public boolean contains(int x, int y) {
		for(int i=0; i<points.size()-1; i++) {
			if(Segment.pointToSegmentDistance(x, y, (int)points.get(i).getX(), (int)points.get(i).getY(), (int)points.get(i+1).getX(), (int)points.get(i+1).getY()) <=3) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		for(int i=0; i<points.size()-1; i++) {
			g.drawLine((int)points.get(i).getX(), (int)points.get(i).getY(), (int)points.get(i+1).getX(), (int)points.get(i+1).getY());
		}
	}

	@Override
	public String toString() {
		String list = "";
		for(Point p: points) {
			list += (int)p.getX() + " " + (int)p.getY() + " ";
		}
		return "polyline "+list+color.getRGB();
	}
}
