package backup_service.distributor.services;

import java.io.IOException;

import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.MessageConstructor;

public class Restore implements IDistribute{
	
	private ChannelManager chnMngr;
	
	public Restore(ChannelManager chnMngr){
		this.chnMngr = chnMngr;
	}
	
	@Override
	public boolean distribute(String line) {//GETCHUNK 
		HeaderInfo header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		/*
		 * Check if there is a copy of that chunk with that fileID
		 * and then SEND the CHUNK
		 * CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body> -> VIA MDR
		 *	chnMngr.getMDR(); 
		 *
		 *	WAIT BEFORE SEND
		 *	if it receives a CHUNK message before that time, it should not send the CHUNK!
		 * */
		byte[] chunkData = null;
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

}
