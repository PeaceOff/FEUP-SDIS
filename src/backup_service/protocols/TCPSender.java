package backup_service.protocols;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import utils.Debug;


public class TCPSender {
	
	public TCPSender(InetAddress inet, int port , byte[] data ) throws IOException{
		Debug.log(0,"TCPSENDER","Message Size:" + data.length);
		Socket cli = new Socket();
		Debug.log("TCPSENDER","HERE1");
		cli.connect(new InetSocketAddress(inet,port),50);
		cli.setSoTimeout(50);
		Debug.log("TCPSENDER","HERE2");
		DataOutputStream dout = new DataOutputStream(cli.getOutputStream());
		Debug.log("TCPSENDER","HERE3");
		dout.write(data);
		Debug.log("TCPSENDER","HERE4");
		cli.close(); 
	}
}
