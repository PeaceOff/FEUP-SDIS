package backup_service.distributor.services;

import java.io.IOException;


import backup_service.distributor.IDistribute;
import backup_service.protocols.BackupHeader;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.MessageConstructor;
import utils.Debug;

public class SaveChunk implements  IDistribute {

	private ChannelManager chnMngr;
	
	private BackupHeader header;
	
	public SaveChunk(ChannelManager chnMngr) {
		this.chnMngr = chnMngr;
	}
	
	@Override
	public boolean distribute(String line) {
		
		header = new BackupHeader(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		Debug.log(1,"PUTCHUNK","Data:" + header);
		
		try {//FAZER WAIT!
			chnMngr.getMC().sendMessage(MessageConstructor.getSTORED(header.fileID, header.chunkNo));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {
		
		Debug.log(2,"PUTCHUNK-DATA","DataSize: " + data.length);
		
	}
	
	
	
}
