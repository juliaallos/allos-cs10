import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Tim Pierson Dartmouth CS 10, provided for Winter 2024
 * @author Julia Allos Dartmouth CS 10, Spring 2024
 */


/**PSUEOCODE:
 * for every shape in sketch, send message to client to add that shape
 * from buffered reader, use handle message function taking line in from client, handle message, broadcast to server
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

			// Tells the client the current state of the world
			if(server.getSketch().getListOfShapes().size() !=0) send("add " + server.getSketch().toString());

			// Keep getting and handling messages from the client
			String message;
			while((message = in.readLine()) != null) {
				System.out.println("received: " + message);
				Messages.handleMessage(server.getSketch(), message);
				server.broadcast(message);
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
}
