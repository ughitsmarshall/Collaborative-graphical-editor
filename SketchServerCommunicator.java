import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.TreeMap;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 * @author Marshall Carey-Matthews CS 10 24W
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			// TODO: YOUR CODE HERE
			Sketch serverSketch = server.getSketch();
			TreeMap<Integer, Shape> map = serverSketch.getShapes();
			for (Integer id : map.navigableKeySet()){
				Shape shape = map.get(id);
				send("DRAWID "+id+" "+serverSketch.getTop()+" "+shape);
			}

			// Keep getting and handling messages from the client
			// TODO: YOUR CODE HERE
			String cmd;
			while ((cmd = in.readLine()) != null){
				takeCmd(cmd);
				System.out.println(serverSketch.getShapes().get(0));
			}


			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//essentially the same thing as the EditorCommunicator
	public synchronized void takeCmd(String cmd){
		Sketch ourSketch = server.getSketch();
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



		} else if (parts[0].equals("MOVE")){
			ourSketch.moveShape(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));

		} else if (parts[0].equals("RECOLOR")){
			ourSketch.recolorShape(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));

		} else if (parts[0].equals("DELETE")){
			ourSketch.deleteShape(Integer.parseInt(parts[1]));
		}

		server.broadcast(cmd);
	}

}
