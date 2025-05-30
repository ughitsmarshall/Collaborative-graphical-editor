import java.awt.*;
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
	// TODO: YOUR CODE HERE

	private List<Segment> segements;

	public Polyline(int x1, int y1, Color color){
		segements = new ArrayList<>();
		segements.add(new Segment(x1, y1, color));
	}

	public Polyline(){
		segements = new ArrayList<>();
	}

	public void addSegment (Segment segment){
		segements.add(segment);
	}

	public void addSegment(int x1, int y1, Color color){
		segements.get(segements.size()-1).setEnd(x1, y1);
		segements.add(new Segment(x1, y1, color));
	}

	@Override
	public void moveBy(int dx, int dy) {
		for (Segment segment : segements){
			segment.moveBy(dx, dy);
		}
	}

	@Override
	public Color getColor() {
		return segements.get(0).getColor();
	}

	@Override
	public void setColor(Color color) {
		for (Segment segment : segements){
			segment.setColor(color);
		}
	}
	
	@Override
	public boolean contains(int x, int y) {
		for (Segment segment : segements){
			if (segment.contains(x, y)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		for (Segment segment : segements){
			segment.draw(g);
		}
	}

	@Override
	public String toString() {
		String theReturn = "polyline ";
		for (Segment segment : segements){
			theReturn += segment;
			theReturn += " ";
		}
		return theReturn.substring(0, theReturn.length()-1);
	}
}
