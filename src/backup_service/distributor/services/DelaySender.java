package backup_service.distributor.services;

import java.io.IOException;
import java.util.Random;

import backup_service.protocols.Subprotocol;
import utils.Debug;

public class DelaySender extends Thread{
	private static Random rnd = new Random();
	private byte[] msg;
	private Subprotocol prot;
	
	public DelaySender(byte[] message, Subprotocol connection){
		msg = message;
		prot = connection;
		this.start();
	}
	
	@Override
	public void run() {
		try {
			
			sleep(rnd.nextInt(401));
			Debug.log(1,"SendThread:","Sending Message");
			prot.sendMessage(msg);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
