package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.HeaderInfo;
import file_managment.FileManager;
import backup_service.protocols.ChannelManager;
import utils.Debug;

public class Stored implements IDistribute {
	
	FileManager fileManager;
	
	public Stored(FileManager mngr){
		fileManager = mngr;
	}
	
	@Override
	public boolean distribute(String line) {
		HeaderInfo header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		fileManager.save_file_chunk_data(header.fileID, header.chunkNo, header.senderID, header.replicationDeg); 
	
		Debug.log(1,"STORED","Data:" + header);
		return true;
	}

	@Override
	public void distribute(byte[] data) {		
	}

}
