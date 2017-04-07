package backup_service.protocols;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import utils.Debug;


public class TCPSender {
	
	public TCPSender(InetAddress inet, int port , byte[] data ) throws IOException{
		Debug.log(0,"TCPSENDER","Message Size:" + data.length);
		Socket cli = new Socket(inet, port);
		DataOutputStream dout = new DataOutputStream(cli.getOutputStream());
		dout.write(data);
		cli.close(); 
	}
	
}
