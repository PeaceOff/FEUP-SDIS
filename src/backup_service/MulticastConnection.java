package backup_service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

import backup_service.protocols.Subprotocol;
import utils.Debug;

public class MulticastConnection extends Thread {
	
	private ConnectionInformation connectionInfo;
	private Subprotocol protocol;
	private InetAddress group;
	
	private MulticastSocket socket;
	
	public MulticastConnection(String groupNPort, Subprotocol protocol) throws IOException{
		connectionInfo = new ConnectionInformation(groupNPort);
		joinGroup();
		this.protocol = protocol;
	}
	
	public MulticastConnection(String group, int port, Subprotocol protocol) throws IOException{
		connectionInfo = new ConnectionInformation(group, port);
		joinGroup();
		this.protocol = protocol;
	}
	
	public ConnectionInformation getConnectionInfo() {
		return connectionInfo;
	}

	private void joinGroup() throws IOException{
		socket = new MulticastSocket(connectionInfo.getPort());
		
		group = InetAddress.getByName(connectionInfo.getIP());
		
		socket.joinGroup(group);
	}
	
	public void sendData(byte[] data) throws IOException{
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, group, connectionInfo.getPort());
		
		socket.send(sendPacket);
	}
	
	public byte[] receiveData() throws IOException{
		byte[] buf = new byte[64*1024];
		DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
		socket.receive(receivePacket);
		//Debug.log(connectionInfo.toString(),""+receivePacket.getLength());
		return Arrays.copyOfRange(buf,0,receivePacket.getLength());
	}
	
	public void run(){
		Debug.log(connectionInfo.toString(),"Running!");
		while(true){
			
			try {
				
				byte[] data = receiveData();
				protocol.receiveMessage(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
