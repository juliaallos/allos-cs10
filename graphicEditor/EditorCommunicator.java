import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 * @author Julia Allos Dartmouth CS 10, Spring 2024
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
			//create handle message object, pass it sketch, pass it line that its going to parse which is the message it gets from send, then repaint
			String message;
			while((message = in.readLine()) != null) {
				System.out.println("Received broadcast " + message);
				Messages.handleMessage(editor.getSketch(), message);
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

	public void add(Shape s) {
		send("add " + s);
	}
	public void move(int s, Point drawfrom, Point movefrom) {
		send("move " + s + " " + (int)drawfrom.getX() + " " + (int)drawfrom.getY() + " " + (int)movefrom.getX() + " " + (int)movefrom.getY());
	}

	public void recolor(int s, Color color) {
		send("recolor " + s + " " + color.getRGB());
	}

	public void delete(int s) {
		send("delete " + s);
	}
	
}
