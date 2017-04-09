package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import backup_service.protocols.MessageConstructor;
import backup_service.protocols.TCPSender;
import file_managment.FileManager;
import utils.Debug;

import java.io.IOException;

public class DeleteFile2 extends BaseService implements IDistribute {

	private HeaderInfo header;

	public DeleteFile2(ChannelManager channelManager,FileManager fileManager,HeaderInfo header) {
		super(channelManager,fileManager);
		this.header = header;
	}

	public DeleteFile2(ChannelManager channelManager,FileManager fileManager) {
		super(channelManager,fileManager);
	}

	@Override
	public BaseService clone() {
		return new DeleteFile2(channelManager,fileManager,header.clone());
	}

	@Override
	public boolean distribute(String line) {
		header = new HeaderInfo(line);

		if(!header.version.equals(ChannelManager.getVersion()))
			return false;

		if(header.senderID == ChannelManager.getServerID())
			return false;

		if(fileManager.do_i_store_it(header.fileID))
			this.clone().start();
		
		return true;
	}

	@Override
	public void distribute(byte[] data) {
	}

	@Override
	public void run() {

		try {

			new TCPSender(channelManager.getMC().getLastAddress(),channelManager.getMC().getPort()+header.senderID, MessageConstructor.getCONFDEL(header.fileID));

		} catch (Exception e) {
			Debug.log("DELETEFILE2","Error sending message to TCP");
			return;
		}

		fileManager.delete_file(header.fileID);
	}
}
