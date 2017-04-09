package backup_service.distributor.services;

import java.io.IOException;
import java.util.Random;

import backup_service.distributor.IMessageListener;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.MessageConstructor;
import file_managment.FileManager;
import utils.Debug;

public class Restore extends BaseService implements IMessageListener{
	
	private Random rnd = new Random();
	private boolean record = false;
	private boolean received = false;
	
	private HeaderInfo header;
	
	public Restore(ChannelManager chnMngr, FileManager fileManager){
		super(chnMngr, fileManager);
		
		//this.chnMngr.getMDR().getDistributor().addListener("CHUNK", this);	
	}
	
	public Restore(ChannelManager chnMngr, FileManager fileManager, HeaderInfo header){
		super(chnMngr, fileManager);
		this.header = header;
		//this.chnMngr.getMDR().getDistributor().addListener("CHUNK", this);	
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.channelManager.getMDR().getDistributor().removeListener("CHUNK", this);
		super.finalize();
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
	public void messageReceived(String line) {
		if(!record)
			return;
		
		HeaderInfo headerTemp = new HeaderInfo(line);
		if(headerTemp.senderID == ChannelManager.getServerID())
			return;
		
		if(headerTemp.fileID.equals(header.fileID))
			received = true;
		
	}
	
	@Override
	public void run() {
		this.channelManager.getMDR().getDistributor().addListener("CHUNK", this);
		byte[] chunkData = fileManager.get_file_chunk(header.fileID, header.chunkNo);
		if(chunkData == null)
			return;
		
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
			return;
		}
		Debug.log(1,"GETCHUNK","Sending CHUNK: " + chunkData.length + "KB");
		
		
		try {
			channelManager.getMDR().sendMessage(MessageConstructor.getCHUNK(header.fileID, header.chunkNo, chunkData));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public BaseService clone() {
		return new Restore(channelManager, fileManager, header.clone());
	}

}
