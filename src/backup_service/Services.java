package backup_service;

import java.io.IOException;

import backup_service.protocols.ChannelManager;
import backup_service.protocols.MessageConstructor;
import utils.Debug;

public class Services {
	
	public static void sendPutChunk(ChannelManager channelManager, String fileID, int chunkNo, int degree, byte[] data) throws IOException{
		byte[] message = MessageConstructor.getPUTCHUNK(fileID, chunkNo, degree, data);
		
		int tries = 0;
		int waitTime = 1;
		while(tries < Server.MAX_RESEND){
			tries++;
			
			channelManager.getMDB().sendMessage(message);
			
			try {//Wait!
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//CHECK RECEPTIONS
			//RETURN IF DONE { RECEPTIONS >= REP_DEGREE
			
			waitTime*=2;
			
		}
		
		Debug.log(0, "SERVER_SENDPUTCHUNK", "Could not reach desired replication degree");
	}
	
}
