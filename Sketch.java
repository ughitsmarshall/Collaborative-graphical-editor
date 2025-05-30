import java.awt.*;
import java.util.TreeMap;

public class Sketch {
    private TreeMap<Integer, Shape> shapes;
    private int top;

    public Sketch(){
        shapes = new TreeMap<>();
        top = 0;
    }

    public TreeMap<Integer, Shape> getShapes(){
        return shapes;
    }

    public synchronized void addShape(Shape shape){
        shapes.put(top, shape);
        top++; //always increase top so that the id for the next shape is guaranteed fresh
    }

    public synchronized void addShapeID(Shape shape, int id){ //this is for when a sketch is initialized, IDs must match
        shapes.put(id, shape);
    }

    public synchronized void setTop(int top){ //for initialization, accounts for deletions prior to initialization
        this.top = top;
    }

    public int getTop(){
        return top;
    }

    public synchronized void deleteShape(Integer id){
        shapes.remove(id); //need to get the id, not the shape itself.
    }

    public synchronized void moveShape(Integer id, int dx, int dy){
        Shape shape = shapes.get(id);
        shape.moveBy(dx, dy);
    }

    public synchronized void recolorShape(Integer id, int rgb){
        Shape shape = shapes.get(id);
        Color color = new Color(rgb);
        shape.setColor(color);
    }




}
