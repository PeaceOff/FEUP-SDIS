package backup_service.protocols;

import java.io.IOException;
import java.util.Arrays;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import utils.Utilities;

public class MessageConstructor {
	private static ByteOutputStream bos = new ByteOutputStream();
	public static byte[] getSTORED(String fileID, int chunkNo){
		bos.reset();
		
		bos.writeAsAscii("STORED");
		bos.writeAsAscii(" " + ChannelManager.getVersion());
		bos.writeAsAscii(" " + ChannelManager.getServerID());
		bos.writeAsAscii(" " + fileID);
		bos.writeAsAscii(" " + chunkNo);
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		
		return Arrays.copyOf(bos.getBytes(), bos.size());
	}
	
	public static byte[] getPUTCHUNK(String fileID, int chunkNo, int replicationDeg, byte[] data) throws IOException{
		bos.reset();
		
		bos.writeAsAscii("PUTCHUNK");
		bos.writeAsAscii(" " + ChannelManager.getVersion());
		bos.writeAsAscii(" " + ChannelManager.getServerID());
		bos.writeAsAscii(" " + fileID);
		bos.writeAsAscii(" " + chunkNo);
		bos.writeAsAscii(" " + replicationDeg);
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		bos.write(data);
		
		return Arrays.copyOf(bos.getBytes(), bos.size());
	}
	
	
}
