package backup_service.distributor.services;

import java.io.IOException;
import java.util.Random;

import backup_service.Services;
import backup_service.distributor.IMessageListener;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import file_managment.FileManager;
import utils.Debug;

public class RemoveChunk extends BaseService implements IMessageListener {
	
	private Random rnd = new Random();
	private Services services;
	private HeaderInfo header;
	
	private boolean proved = false;
	private boolean record = false;
	
	public RemoveChunk(ChannelManager channelManager, FileManager fileManager){
		super(channelManager,fileManager);
		//this.channelManager.getMDB().getDistributor().addListener("PUTCHUNK", this);
	}
	
	public RemoveChunk(ChannelManager channelManager, FileManager fileManager, HeaderInfo header){
		super(channelManager,fileManager);
		this.header = header;
		//this.channelManager.getMDB().getDistributor().addListener("PUTCHUNK", this);

	}
	
	@Override
	protected void finalize() throws Throwable {
		this.channelManager.getMDB().getDistributor().removeListener("PUTCHUNK", this);
		super.finalize();
	}
	
	private boolean waitForResend(HeaderInfo info){
		proved = false;
		record = true;
		
		try {
			Thread.sleep(rnd.nextInt(401));
		} catch (InterruptedException e) {
			
		}
		
		record = false;
		return proved;
		
	}
	
	@Override
	public boolean distribute(String line) {//REMOVED
		header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		Debug.log("REMOVECHUNK","Init" + header);
		
		
		if(!fileManager.peer_deleted_chunk(header.fileID, header.chunkNo, header.senderID)){
			return true;
		}
		
		/* REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
		 * 
		 * UPDATE LOCAL COUNT! Return true if don't exist!
		 * 
		 * IF DROPS
		 * 
		 * Do the Backup subprotocol!
		 * WAIT BEFORE DOING THAT!
		 * IF receives PUTCHUNK for the same file STOP imediately
		 * */
		this.clone().start();
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {}

	@Override
	public void messageReceived(String line) {
		
		if(!record)
			return;
		
		HeaderInfo headerTemp = new HeaderInfo(line);
		if(headerTemp.senderID == ChannelManager.getServerID())
			return;
		
		if(headerTemp.fileID.equals(header.fileID))
			proved = true;
		
	}
	
	@Override
	public void run() {
		byte[] fileData = fileManager.get_file_chunk(header.fileID, header.chunkNo);
		if(fileData == null)
			return;
		this.channelManager.getMDB().getDistributor().addListener("PUTCHUNK", this);
		this.services = new Services(channelManager,fileManager);
		if(waitForResend(header)){
			Debug.log(1,"REMOVECHUNK","COULD NOT SEND!");
			return;
		}
		Debug.log(1,"REMOVECHUNK","RECEIVING!");
		
		int replication_degree = fileManager.getMapper().get_rep_degree(header.fileID, header.chunkNo);

		fileManager.add_file_in_progress(header.fileID,header.chunkNo,replication_degree);

		try {
			services.sendPutChunk(header.fileID, header.chunkNo, replication_degree, fileData,true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fileManager.remove_file_in_progress(header.fileID);

	}
	
	@Override
	public BaseService clone() { 
		return new RemoveChunk(channelManager, fileManager, header.clone());
	}

}
