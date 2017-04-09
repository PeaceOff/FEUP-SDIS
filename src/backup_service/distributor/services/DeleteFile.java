package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import file_managment.FileManager;

public class DeleteFile implements IDistribute {
	
	private FileManager fileManager;
	
	public DeleteFile(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	@Override
	public boolean distribute(String line) {
		HeaderInfo header = new HeaderInfo(line);

		if(!header.version.equals(ChannelManager.getVersion()))
			return false;

		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		fileManager.delete_file(header.fileID);
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {
	}
	
}
