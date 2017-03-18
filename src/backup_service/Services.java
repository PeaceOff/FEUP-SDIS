package backup_service;

import java.io.IOException;
import java.util.HashSet;

import backup_service.distributor.Distributor;
import backup_service.distributor.IMessageListener;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.MessageConstructor;
import utils.Debug;

public class Services implements IMessageListener {
	
	private static int counter = 0;
	
	public Distributor distr;
	
	private ChannelManager channelManager;
	
	private int id = 0;
	private boolean record = false;
	private String fileID;
	private HashSet<Integer> confirmations = new HashSet<>();
	
	public Services (ChannelManager chnl){
		channelManager = chnl;
		chnl.getMC().getDistributor().addListener("STORED", this);
		id = counter++;
	}
	
	public int getId() {
		return id;
	}

	@Override
	protected void finalize() throws Throwable {
		distr.removeListener("STORED", this);
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
			
			if(getReceptions() >= degree){
				Debug.log("SERVICES_SENDPUTCHUNK","Finished BACKUP! Receptions:" +getReceptions());
				record = false;
				return;
			}
			Debug.log("SERVICES_SENDPUTCHUNK","Not Enought Confirmations!" + getReceptions() + "Waited:" + waitTime +"seconds");
			
			waitTime*=2;
			
		}
		record = false;
		Debug.log(0, "SERVICES_SENDPUTCHUNK", "Could not reach desired replication degree");
	}
	
	private int getReceptions(){
		return confirmations.size();
	}

	@Override
	public void messageReceived(String line) {
		if(!record) return;
		
		HeaderInfo header = new HeaderInfo(line);
		if(header.fileID.equals(this.fileID))
			confirmations.add(header.senderID);
		
	}
	
}
