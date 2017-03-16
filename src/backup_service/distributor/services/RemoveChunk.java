package backup_service.distributor.services;

import java.io.IOException;

import backup_service.Services;
import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;

public class RemoveChunk implements IDistribute {
	
	private ChannelManager channelManager;
	
	public RemoveChunk(ChannelManager channelManager){
		this.channelManager = channelManager;
	}
	
	@Override
	public boolean distribute(String line) {
		HeaderInfo header = new HeaderInfo(line);
		if(header.senderID == ChannelManager.getServerID())
			return false;
		
		/* REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
		 * UPDATE LOCAL COUNT!
		 * IF DROPS
		 * Do the Backup subprotocol!
		 * WAIT BEFORE DOING THAT!
		 * IF receives PUTCHUNK for the same file STOP imediately
		 * */
		
		int replication_degree = 0;
		byte[] fileData = null;
		
		try {
			
			Services.sendPutChunk(channelManager, header.fileID, header.chunkNo, replication_degree, fileData);
		
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
