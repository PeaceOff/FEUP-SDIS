package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import file_managment.FileManager;

public abstract class BaseService extends Thread implements IDistribute{
	
	protected ChannelManager channelManager;
	protected FileManager fileManager;
	
	public BaseService (ChannelManager chnlmngr, FileManager fileManager){
		this.channelManager = chnlmngr;
		this.fileManager = fileManager;
	}
	
	public abstract BaseService clone();
	
}
