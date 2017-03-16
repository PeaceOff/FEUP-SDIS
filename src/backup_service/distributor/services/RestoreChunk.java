package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import file_managment.FileManager;

public class RestoreChunk implements IDistribute {
	
	private FileManager fileManager;
	private HeaderInfo header;
	
	public RestoreChunk(FileManager fileManager){
		this.fileManager = fileManager;
	}
	
	
	@Override
	public boolean distribute(String line) {//CHUNK
		
		header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {
		// TODO Chunk DATA!
		//Save Data
	}

}
