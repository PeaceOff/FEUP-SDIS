package backup_service.distributor.services;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import backup_service.distributor.IMessageListener;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.MessageConstructor;
import file_managment.FileManager;
import utils.Debug;

public class SaveChunk extends BaseService implements IMessageListener {
	
	private static Random rnd = new Random();
	
	private HeaderInfo header;
	
	private boolean record = false;
	private HashSet<Integer> found = new HashSet<Integer>();
	
	public SaveChunk(ChannelManager chnMngr, FileManager fileManager) {
		super(chnMngr, fileManager);
		//chnMngr.getMC().getDistributor().addListener("STORED", this);
	}
	
	public SaveChunk(ChannelManager chnMngr, FileManager fileManager, HeaderInfo header) {
		super(chnMngr, fileManager);
		this.header = header;
		record = false;
	}
	
	@Override
	protected void finalize() throws Throwable {
		channelManager.getMC().getDistributor().removeListener("STORED", this);
		super.finalize();
	}
	
	@Override
	public boolean distribute(String line) {
		
		header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		Debug.log(1,"PUTCHUNK","Datas:" + header);
		this.channelManager.getMDB().expectBody = true;
		
		return true;
	}
	
	@Override
	public void distribute(byte[] data) {
		
		Debug.log(2,"PUTCHUNK-DATA","DataSize: " + data.length);
		
		try {
			
			if(fileManager.save_chunk(data, header.fileID, header.chunkNo, header.senderID, header.replicationDeg)){
				this.clone().start();
			}
			
		} catch (IOException e) {
			Debug.log(2,"PUTCHUNK", "Failed to SAVE CHUNK!!!");
			e.printStackTrace();
		}
		
	}
	
	private int waitForOtherConfirmations(){
		found.clear();
		record = true;
		
		try {
			Thread.sleep(rnd.nextInt(401));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		record=  false;
		return found.size();
	}
	

	@Override
	public void messageReceived(String line) {
		if(!record)
			return;
		
		HeaderInfo header2 = new HeaderInfo(line);
		if(header2.senderID == ChannelManager.getServerID())
			return;
		
		if(header2.fileID.equals(header.fileID) && header2.chunkNo == header.chunkNo)
			found.add(header2.senderID);
	}


	
	@Override
	public BaseService clone() {
		
		return new SaveChunk(channelManager, fileManager, header.clone());
	}
	
	@Override
	public void run() {
		channelManager.getMC().getDistributor().addListener("STORED", this);
		try {
			if(waitForOtherConfirmations() < header.replicationDeg)
				channelManager.getMC().sendMessage(MessageConstructor.getSTORED(header.fileID, header.chunkNo));
			else
				fileManager.delete_file_chunk(header.fileID, header.chunkNo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
