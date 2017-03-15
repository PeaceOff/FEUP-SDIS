package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;

public class Restore implements IDistribute{

	@Override
	public boolean distribute(String line) {//GETCHUNK 
		HeaderInfo header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {
		// TODO Auto-generated method stub
		
	}

}
