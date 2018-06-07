package ChatServer;
import java.io.*;
import java.net.*;


public class chatServer {
	public static void main(String args[]) throws IOException{
		if (args.length != 1) {
			System.err.println("Unsupported inputs, Expected: chatServer <port number>");
			System.exit(1);
	}
		int portNumber = Integer.parseInt(args[0]);
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			Socket clientSocket = serverSocket.accept();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
		}
		catch(IOException e){
			System.out.println("Port " + portNumber + " is not available" );
		}
	}
}
