package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import file_managment.FileManager;
import utils.Debug;

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
		
		Debug.log(1,"CHUNK","Received[" + header.chunkNo + "]" + header.fileID);
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {
		// TODO Chunk DATA!
		//Save Data

		Debug.log(2,"CHUNK"," ChunkSize " + header.fileID.length());
		
		fileManager.getChunkManager().AddChunk(header.fileID, header.chunkNo, data);
		
	}

}
