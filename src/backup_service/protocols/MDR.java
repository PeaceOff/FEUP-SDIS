package backup_service.protocols;

import java.io.IOException;

public class MDR extends Subprotocol {

	public MDR(String ipNport, ChannelManager channelManager) throws IOException {
		super(ipNport, channelManager);
	}

	@Override
	public void receiveMessage(byte[] message) {
		
	}

}
