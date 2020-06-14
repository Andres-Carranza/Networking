package andres.networking.client;
/*
 * Andres Carranza
 * 5/28/2019
 * This class connects with the server and DrawingSurfacetains that connection while its running
 */
import java.io.*;
import java.net.*;

import andres.networking.server.Server;
public class Client {

	private NetworkListener listener;
	private boolean closeConnection;
	private Socket serverConnection;
	private BufferedReader serverIn;
	private PrintStream serverOut;
	private String clientIp;

	public Client(String serverIp, NetworkListener listener) throws IOException{
		this.listener = listener;
		closeConnection = false;
		clientIp = InetAddress.getLocalHost().getHostAddress();
		serverConnection = new Socket(serverIp, Server.SERVER_PORT_NUMBER);
		serverIn = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
		serverOut = new PrintStream(serverConnection.getOutputStream());

		Thread receiveMessage = new Thread(new ReceiveMessage());
		receiveMessage.start();
	}

	/**
	 * Sends a message to the specified recipient
	 * 
	 * @param recipient the username or IP of the recipient
	 * @param ID the identifier of the message
	 * @param message the message to be sent
	 */
	public void sendMessage(String recipient, String ID, String message) {
		serverOut.println(recipient + ":" + ID + ":" + message);
	}

	/**
	 * Receives a message from the server and parses it
	 * 
	 * @param message message received
	 */
	public void messageReceived(String message) {
		int i1 = message.indexOf(":");
		int i2 = message.indexOf(":", i1 + 1);

		String recipient = message.substring(0, i1);
		String ID  = message.substring(i1 + 1, i2);
		message = message.substring(i2 + 1);

		if(ID.equals(Server.CHECK_CONNECTION)) {
			checkConnection();
			return;
		}

		listener.messageReceived(recipient,ID,message);
	}


	/** 
	 * Returns the current clients ip address
	 * 
	 * @return the clients ip address
	 */
	public String getClientIp() {
		return clientIp;
	}

	//Requests server to set name
	public void setName(String name) {
		sendMessage(Server.SERVER_ID.toString(),Server.SET_NAME,name);
	}

	public void checkConnection() {
		sendMessage(Server.SERVER_ID, Server.CHECK_CONNECTION, "true");
	}

	private  class ReceiveMessage implements Runnable{
		@Override
		public void run() {
			try {
				while(!closeConnection) {
					String message = serverIn.readLine();
					if(message != null)
						messageReceived(message);
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}
