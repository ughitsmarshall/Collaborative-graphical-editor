import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 * @author Marshall Carey-Matthews CS 10 24W
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			// TODO: YOUR CODE HERE
			String line;
			while ((line = in.readLine()) != null) {
				takeCmd(line);
				editor.repaint();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}	

	// Send editor requests to the server
	// TODO: YOUR CODE HERE
	public synchronized void takeCmd(String cmd){
		Sketch ourSketch = editor.getSketch();
		String[]parts = cmd.split(" ");


		if (parts[0].equals("DRAW")){
			Shape newShape = null;

			if (parts[1].equals("ellipse")){
				newShape = new Ellipse(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]),
						Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), new Color(Integer.parseInt(parts[6])));

			} else if (parts[1].equals("rectangle")){
				newShape = new Rectangle(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]),
						Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), new Color(Integer.parseInt(parts[6])));

			} else if (parts[1].equals("segment")){
				newShape = new Segment(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]),
						Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), new Color(Integer.parseInt(parts[6])));

			}else if (parts[1].equals("polyline")){
				newShape = new Polyline();
				for (int i = 3; i < parts.length; i += 6){ //loop to add all the segments
					Segment newSeg = new Segment(Integer.parseInt(parts[i]), Integer.parseInt(parts[i+1]),
							Integer.parseInt(parts[i+2]), Integer.parseInt(parts[i+3]), new Color(Integer.parseInt(parts[i+4])));
					((Polyline) newShape).addSegment(newSeg);
				}
			}
			if (newShape != null) ourSketch.addShape(newShape);


		//This command should only occur when initializing the sketch as someone connects to the server
		} else if (parts[0].equals("DRAWID")){
			Shape newShape = null;

			if (parts[3].equals("ellipse")){
				newShape = new Ellipse(Integer.parseInt(parts[4]), Integer.parseInt(parts[5]),
						Integer.parseInt(parts[6]), Integer.parseInt(parts[7]), new Color(Integer.parseInt(parts[8])));

			} else if (parts[3].equals("rectangle")){
				newShape = new Rectangle(Integer.parseInt(parts[4]), Integer.parseInt(parts[5]),
						Integer.parseInt(parts[6]), Integer.parseInt(parts[7]), new Color(Integer.parseInt(parts[8])));

			} else if (parts[3].equals("segment")){
				newShape = new Segment(Integer.parseInt(parts[4]), Integer.parseInt(parts[5]),
						Integer.parseInt(parts[6]), Integer.parseInt(parts[7]), new Color(Integer.parseInt(parts[8])));

			}else if (parts[3].equals("polyline")){
				newShape = new Polyline();
				for (int i = 5; i < parts.length; i += 6){ //loop to add all the segments
					Segment newSeg = new Segment(Integer.parseInt(parts[i]), Integer.parseInt(parts[i+1]),
							Integer.parseInt(parts[i+2]), Integer.parseInt(parts[i+3]), new Color(Integer.parseInt(parts[i+4])));
					((Polyline) newShape).addSegment(newSeg);
				}
			}
			ourSketch.setTop(Integer.parseInt(parts[2]));
			if (newShape != null) ourSketch.addShapeID(newShape, Integer.parseInt(parts[1]));

		} else if (parts[0].equals("MOVE")){
			ourSketch.moveShape(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

		} else if (parts[0].equals("RECOLOR")){
			ourSketch.recolorShape(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));

		} else if (parts[0].equals("DELETE")){
			ourSketch.deleteShape(Integer.parseInt(parts[1]));
		}
	}

	
}
