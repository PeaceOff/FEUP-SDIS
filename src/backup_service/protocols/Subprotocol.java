package backup_service.protocols;

import java.io.IOException;

import backup_service.MulticastConnection;

public abstract class Subprotocol {
	private static String version;
	private static int server_id;
	private MulticastConnection connection;
	
	public Subprotocol(String version, int server_id, String ipNport) throws IOException{
		connection = new MulticastConnection(ipNport, this);
		this.version = version;
		this.server_id = server_id;
	}
	
	public void start(){
		connection.start();
	}
	
	public static String getVersion() {
		return version;
	}

	public static int getServer_id() {
		return server_id;
	}
	
	public MulticastConnection getConnection() {
		return connection;
	}

	public abstract void receiveMessage(byte[] message);
	
}
