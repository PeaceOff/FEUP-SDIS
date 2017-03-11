package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.BackupHeader;
import backup_service.protocols.ChannelManager;
import utils.Debug;

public class Stored implements IDistribute {
	
	
	
	@Override
	public boolean distribute(String line) {
		BackupHeader header = new BackupHeader(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		Debug.log(1,"STORED","Data:" + header);
		return true;
	}

	@Override
	public void distribute(byte[] data) {		
	}

}
