package backup_service.distributor.services;

import java.io.IOException;
import java.util.Random;

import backup_service.distributor.IDistribute;
import backup_service.distributor.IMessageListener;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.MessageConstructor;
import file_managment.FileManager;
import utils.Debug;

public class Restore implements IDistribute, IMessageListener{
	
	private Random rnd = new Random();
	
	private ChannelManager chnMngr;
	private FileManager fileManager;
	private boolean record = false;
	private boolean received = false;
	
	private HeaderInfo header;
	
	public Restore(ChannelManager chnMngr, FileManager fileManager){
		this.chnMngr = chnMngr;
		this.chnMngr.getMDR().getDistributor().addListener("CHUNK", this);	
		this.fileManager = fileManager;
	}
	
	public boolean waitForMessage(){
		received = false;
		record = true;
		
		try {
			Thread.sleep(rnd.nextInt(401));
		} catch (InterruptedException e) {
		}
		
		record = false;
		return received;
	}
	
	@Override
	public boolean distribute(String line) {//GETCHUNK 
		header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;

		if(fileManager.is_my_file(header.fileID))
			return true;

		Debug.log(1,"GETCHUNK", header.toString());
		
		byte[] chunkData = fileManager.get_file_chunk(header.fileID, header.chunkNo);
		if(chunkData == null)
			return true;
		
		/*
		 * Check if there is a copy of that chunk with that fileID
		 * and then SEND the CHUNK
		 * CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body> -> VIA MDR
		 *	chnMngr.getwaMDR(); 
		 *
		 *	WAIT BEFORE SEND
		 *	if it receives a CHUNK message before that time, it should not send the CHUNK!
		 * */
		
		if(waitForMessage()){
			Debug.log(1,"GETCHUNK","CHUNK already sent!");
			return true;
		}
		Debug.log(1,"GETCHUNK","Sending CHUNK: " + chunkData.length + "KB");
		
		
		try {
			chnMngr.getMDR().sendMessage(MessageConstructor.getCHUNK(header.fileID, header.chunkNo, chunkData));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			received = true;
		
	}

}
