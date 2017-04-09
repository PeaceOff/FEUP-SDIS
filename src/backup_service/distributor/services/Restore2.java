package backup_service.distributor.services;

import java.io.IOException;

import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.MessageConstructor;
import backup_service.protocols.TCPSender;

import file_managment.FileManager;

import utils.Debug;

public class Restore2 extends BaseService{
	
	private HeaderInfo header;
	
	public Restore2(ChannelManager chnMngr, FileManager fileManager){
		super(chnMngr, fileManager);
	}
	
	public Restore2(ChannelManager chnMngr, FileManager fileManager, HeaderInfo header){
		super(chnMngr, fileManager);
		this.header = header;
	}
	
	@Override
	public boolean distribute(String line) {//GETCHUNK 
		header = new HeaderInfo(line);

		if(!header.version.equals(ChannelManager.getVersion()))
			return false;


		if(header.senderID == ChannelManager.getServerID())
			return false;

		if(fileManager.is_my_file(header.fileID))
			return true;

		Debug.log(1,"GETCHUNK", header.toString());
		
		this.clone().start();
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {}
	
	@Override
	public void run() {
		byte[] chunkData = fileManager.get_file_chunk(header.fileID, header.chunkNo);
		if(chunkData == null)
			return;
		
		try {
			new TCPSender(this.channelManager.getMC().getLastAddress(),channelManager.getMDR().getPort() + header.senderID, MessageConstructor.getCHUNK(header.fileID, header.chunkNo, chunkData));
			
		} catch (IOException e1){ 
			Debug.log(1,"GETCHUNK","CHUNK already sent!");
			return;
		}
		Debug.log(1,"GETCHUNK","Sending CHUNK: " + chunkData.length + "KB");
	}
	
	@Override
	public BaseService clone() {
		return new Restore2(channelManager, fileManager, header.clone());
	}

}
