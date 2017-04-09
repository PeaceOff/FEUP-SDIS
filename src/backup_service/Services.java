package backup_service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import backup_service.distributor.IMessageListener;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.MessageConstructor;
import file_managment.FileManager;
import utils.Debug;

public class Services implements IMessageListener {
	
	private static int counter = 0;
	
	private ChannelManager channelManager;
	
	private int id = 0;
	private boolean record = false;
	private String fileID;
	private HashSet<Integer> confirmations;
	private FileManager fileManager;
	
	public Services (ChannelManager chnl, FileManager fM){
		channelManager = chnl;
		channelManager.getMC().getDistributor().addListener("STORED", this);
		id = counter++;
		fileManager = fM;
		confirmations = new HashSet<>();
	}
	
	public int getId() {
		return id;
	}

	@Override
	protected void finalize() throws Throwable {
		channelManager.getMC().getDistributor().removeListener("STORED", this);
		super.finalize();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!(obj instanceof Services)) return false;
		
		Services svc = (Services)obj;
		return svc.getId() == id;
	}
	
	public void sendPutChunk(String fileID, int chunkNo, int degree, byte[] data) throws IOException{
		sendPutChunk(fileID, chunkNo, degree, data, false);
	}
	
	
	public void sendPutChunk(String fileID, int chunkNo, int degree, byte[] data, boolean containsChunk) throws IOException{
		record = true;
		confirmations.clear();
		this.fileID = fileID;
		
		byte[] message = MessageConstructor.getPUTCHUNK(fileID, chunkNo, degree, data);
		
		int tries = 0;
		int waitTime = 1;
		while(tries < Server.MAX_RESEND){
			tries++;
			
			channelManager.getMDB().sendMessage(message);
			
			try {//Wait!
				Thread.sleep(waitTime * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
			int i = 0;
			if(containsChunk)
				i=1;
			
			if(getReceptions() + i >= degree){
				Debug.log("SERVICES_SENDPUTCHUNK[ " + chunkNo + " ]","Finished BACKUP! Receptions:" +getReceptions());
				fileManager.add_chunk_rep(fileID,chunkNo,getConfirmations());
				record = false;
				return;
			}
			Debug.log("SERVICES_SENDPUTCHUNK[ " + chunkNo + " ]","Not Enought Confirmations!" + getReceptions() + "Waited:" + waitTime +"seconds");
			
			waitTime*=2;
			
		}
		record = false;
		Debug.log(0, "SERVICES_SENDPUTCHUNK[ " + chunkNo + " ]", "Could not reach desired replication degree");
	}
	
	private int getReceptions(){
		return confirmations.size();
	}

	private HashSet<Integer> getConfirmations(){
		return confirmations;
	}

	@Override
	public void messageReceived(String line) {
		if(!record) return;
		
		HeaderInfo header = new HeaderInfo(line);
		if(header.fileID.equals(this.fileID))
			confirmations.add(header.senderID);
		
	}
	
}
