import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.TreeMap;

/**
 * Client-server graphical editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 */

public class Editor extends JFrame {	
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private Sketch sketch;						// holds and handles all the completed objects
	private int currId = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged
	private int moveX = 0;						//for sending out move message
	private int moveY = 0;						//for sending out move message




	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

		// Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};
		
		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});		

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});
		
		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "polyline", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}

	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		// TODO: YOUR CODE HERE
		TreeMap<Integer, Shape> shapes = sketch.getShapes();
		for (int id : shapes.navigableKeySet()){
			Shape shape = shapes.get(id);
			sketch.addShapeID(shape, id);
		}
		if (curr != null){
			curr.draw(g);
		}
	}

	// Helpers for event handlers
	
	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {
		// TODO: YOUR CODE HERE
		// In drawing mode, start drawing a new shape
		// In moving mode, start dragging if clicked in the shape
		// In recoloring mode, change the shape's color if clicked in it
		// In deleting mode, delete the shape if clicked in it
		// Be sure to refresh the canvas (repaint) if the appearance has changed

		if (mode == Mode.DRAW) {
			drawFrom = p;
			if (shapeType.equals("ellipse")){
				curr = new Ellipse(p.x,p.y,color);
			} else if (shapeType.equals("rectangle")){
				curr = new Rectangle(p.x, p.y, color);

			} else if (shapeType.equals("segment")){
				curr = new Segment(p.x, p.y, color);

			} else if (shapeType.equals("polyline")){
				curr = new Polyline(p.x, p.y, color);
			}
		}
		else if (mode == Mode.MOVE) {
			//move if clicked in shape (greatest id has priority)
			moveFrom = p;
			for (int id : sketch.getShapes().descendingKeySet()){
				if (sketch.getShapes().get(id).contains(p.x, p.y)){
					curr = sketch.getShapes().get(id);
					currId = id;
					break;
				}
			}
		}
		else if (mode == Mode.RECOLOR) {
			for (int id : sketch.getShapes().descendingKeySet()){
				if (sketch.getShapes().get(id).contains(p.x, p.y)){
					curr = sketch.getShapes().get(id);
					curr.setColor(color);
					currId = id;
					break;
				}
			}
		}
		else if (mode == Mode.DELETE) {
			for (int id : sketch.getShapes().descendingKeySet()){
				if (sketch.getShapes().get(id).contains(p.x, p.y)){
					curr = sketch.getShapes().get(id);
					currId = id;
					break;
				}
			}
		}
		repaint();
	}

	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {
		// TODO: YOUR CODE HERE
		// In moving mode, shift the object and keep track of where next step is from
		// Be sure to refresh the canvas (repaint) if the appearance has changed
		if (mode == Mode.DRAW) {
			//draw
			if (shapeType.equals("ellipse")){
				((Ellipse) curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
			} else if (shapeType.equals("rectangle")){
				((Rectangle) curr).setCorners(drawFrom.x, drawFrom.y, p.x, p.y);
			} else if (shapeType.equals("segment")){
				((Segment) curr).setEnd(p.x, p.y);
			} else if (shapeType.equals("polyline")){
				((Polyline) curr).addSegment(p.x, p.y, color);
			}
		}
		else if (mode == Mode.MOVE) {
			if (moveFrom != null && curr != null) {
				curr.moveBy(p.x - moveFrom.x, p.y - moveFrom.y);
				moveX += p.x - moveFrom.x;
				moveY += p.y - moveFrom.y;
				moveFrom = p;
			}
		}
		repaint();
	}

	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it		
	 */
	private void handleRelease() {
		// TODO: YOUR CODE HERE
		// send out command to server
		String command = null;
		if (mode == Mode.DRAW){
			command = "DRAW " + curr;
		} else if (mode == Mode.MOVE){
			if (curr != null) curr.moveBy(-moveX, -moveY);
			command = "MOVE " + currId +" "+moveX+" "+moveY;
		} else if (mode == Mode.RECOLOR){
			command = "RECOLOR " + currId + " "+color.getRGB();
		} else if (mode == Mode.DELETE){
			command = "DELETE " + currId;
		}


		if(curr != null)comm.send(command);


		//set curr back to null
		curr = null;
		currId = -1;
		moveFrom = null;
		drawFrom = null;
		moveX = 0;
		moveY = 0;
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});	
	}
}
