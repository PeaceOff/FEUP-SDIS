package backup_service.protocols;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import backup_service.MulticastConnection;
import backup_service.distributor.Distributor;
import utils.Utilities;

public class Subprotocol {
	private MulticastConnection connection;
	private ChannelManager channelManager;
	private Distributor distributor;
	public Subprotocol(String ipNport, ChannelManager channelManager, Distributor distributor) throws IOException{
		connection = new MulticastConnection(ipNport, this);
		this.channelManager = channelManager;
		this.distributor= distributor;
	}
	
	public void start(){
		connection.start();
	}
	
	public Distributor getDistributor(){
		return distributor;
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
	
	public Subprotocol getMC(){
		return channelManager.getMC();
	}
	public Subprotocol getMDB(){
		return channelManager.getMDB();
	}
	public Subprotocol getMDR(){
		return channelManager.getMDR();
	}
	
	public synchronized void sendMessage(byte[] message) throws IOException{
		this.connection.sendData(message);
	}
	
	public void receiveMessage(byte[] message){
		ByteArrayInputStream bis = new ByteArrayInputStream(message);
		
		String line = null;
		while( line==null || !line.equals("")){
			line = Utilities.getLine(bis);
			if(!distributor.routeLine(line)){
				return;
			}
		}
		
		if(bis.available() > 0){
			byte[] lastBytes = new byte[bis.available()];
			try {
				bis.read(lastBytes);
			} catch (IOException e) {
				System.out.println("Could not read last bytes!");
				e.printStackTrace();
			}
			distributor.routeBytes(lastBytes);
		}
		
	}
	
}
