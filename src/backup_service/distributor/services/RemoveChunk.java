package backup_service.distributor.services;

import java.io.IOException;
import java.util.Random;

import backup_service.Services;
import backup_service.distributor.IDistribute;
import backup_service.distributor.IMessageListener;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import file_managment.FileManager;
import utils.Debug;

public class RemoveChunk implements IDistribute, IMessageListener {
	
	private Random rnd = new Random();
	
	private ChannelManager channelManager;
	private Services services;
	private HeaderInfo header;
	private FileManager fileManager;
	
	private boolean proved = false;
	private boolean record = false;
	
	public RemoveChunk(ChannelManager channelManager, FileManager fileManager){
		this.channelManager = channelManager;
		this.channelManager.getMDB().getDistributor().addListener("PUTCHUNK", this);
		this.services = new Services(channelManager,fileManager);
		this.fileManager=fileManager;
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
	
	public void sendPutChunk(HeaderInfo header, int replication_degree, byte[] fileData){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					services.sendPutChunk(header.fileID, header.chunkNo, replication_degree, fileData);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}).start();
		
		
	}
	
	@Override
	public boolean distribute(String line) {
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
		
		
		if(waitForResend(header)){
			Debug.log(1,"REMOVECHUNK","COULD NOT SEND!");
			
			return true;
		}
		Debug.log(1,"REMOVECHUNK","SENDING!");
		
		//GET replication deg and DATA!
		int replication_degree = 3;
		byte[] fileData = new byte[]{1,2,3};
		
		sendPutChunk(header, replication_degree, fileData);

		
		return true;
	}

	@Override
	public void distribute(byte[] data) {
		// TODO Auto-generated method stub
		
	}

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

}
