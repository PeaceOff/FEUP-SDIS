package backup_service.distributor.services;



import java.io.IOException;

import backup_service.distributor.IDistribute;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.MessageConstructor;
import file_managment.FileManager;
import utils.Debug;

public class SaveChunk implements  IDistribute {

	private ChannelManager chnMngr;
	
	private HeaderInfo header;
	
	private FileManager fileManager;
	
	public SaveChunk(ChannelManager chnMngr, FileManager fileManager) {
		this.chnMngr = chnMngr;
		this.fileManager = fileManager;
	}
	
	@Override
	public boolean distribute(String line) {
		
		header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		Debug.log(1,"PUTCHUNK","Datas:" + header);
		
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {
		
		Debug.log(2,"PUTCHUNK-DATA","DataSize: " + data.length);
		
		try {
			
			if(fileManager.save_chunk(data, header.fileID, header.chunkNo, header.senderID, header.replicationDeg))
				new DelaySender(MessageConstructor.getSTORED(header.fileID, header.chunkNo), chnMngr.getMC());
				
			
		} catch (IOException e) {
			Debug.log(2,"PUTCHUNK", "Failed to SAVE CHUNK!!!");
			e.printStackTrace();
		}
		
	}
	
	
	
}
