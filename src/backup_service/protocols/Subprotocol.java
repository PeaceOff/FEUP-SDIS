package backup_service.protocols;

import java.io.IOException;

import backup_service.MulticastConnection;

public abstract class Subprotocol {
	private MulticastConnection connection;
	private ChannelManager channelManager;
	
	public Subprotocol(String ipNport, ChannelManager channelManager) throws IOException{
		connection = new MulticastConnection(ipNport, this);
		this.channelManager = channelManager;
	}
	
	public void start(){
		connection.start();
	}
	
	public static String getVersion() {
		return ChannelManager.getVersion();
	}

	public static int getServerID() {
		return ChannelManager.getServerID();
	}
	
	public MulticastConnection getConnection() {
		return connection;
	}
	
	public MC getMC(){
		return channelManager.getMC();
	}
	public MDB getMDB(){
		return channelManager.getMDB();
	}
	public MDR getMDR(){
		return channelManager.getMDR();
	}
	
	public void sendMessage(byte[] message) throws IOException{
		this.connection.sendData(message);
	}
	
	public abstract void receiveMessage(byte[] message);
	
}
