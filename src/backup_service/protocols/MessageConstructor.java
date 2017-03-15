package backup_service.protocols;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import utils.Utilities;

public class MessageConstructor {
	
	private static ByteOutputStream bos = new ByteOutputStream();
	
	public static final HashMap<String, Integer> commandArgs;
	static
	{
		commandArgs = new HashMap<String, Integer>();
		commandArgs.put("STORED", 4);
		commandArgs.put("PUTCHUNK", 5);
		commandArgs.put("GETCHUNK", 4);
		commandArgs.put("CHUNK", 4);
		commandArgs.put("DELETE", 3);
		commandArgs.put("REMOVE", 4);
	}
	
	public static int getArgumentNumber(String name){
		if(commandArgs.containsKey(name))
			return commandArgs.get(name);
		return -1;
	}
	
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
	
	public static byte[] getGETCHUNK(String fileID, int chunkNo) throws IOException{
		bos.reset();
		
		bos.writeAsAscii("GETCHUNK");
		bos.writeAsAscii(" " + ChannelManager.getVersion());
		bos.writeAsAscii(" " + ChannelManager.getServerID());
		bos.writeAsAscii(" " + fileID);
		bos.writeAsAscii(" " + chunkNo);
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		
		return Arrays.copyOf(bos.getBytes(), bos.size());
	}
	
	public static byte[] getCHUNK(String fileID, int chunkNo, byte[] data) throws IOException{
		bos.reset();
		
		bos.writeAsAscii("CHUNK");
		bos.writeAsAscii(" " + ChannelManager.getVersion());
		bos.writeAsAscii(" " + ChannelManager.getServerID());
		bos.writeAsAscii(" " + fileID);
		bos.writeAsAscii(" " + chunkNo);
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		bos.write(data);
		
		return Arrays.copyOf(bos.getBytes(), bos.size());
	}
	
	public static byte[] getDELETE(String fileID) throws IOException{
		bos.reset();
		
		bos.writeAsAscii("DELETE");
		bos.writeAsAscii(" " + ChannelManager.getVersion());
		bos.writeAsAscii(" " + ChannelManager.getServerID());
		bos.writeAsAscii(" " + fileID);
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		
		return Arrays.copyOf(bos.getBytes(), bos.size());
	}
	
	public static byte[] getREMOVED(String fileID, int chunkNo) throws IOException{
		bos.reset();
		
		bos.writeAsAscii("REMOVED");
		bos.writeAsAscii(" " + ChannelManager.getVersion());
		bos.writeAsAscii(" " + ChannelManager.getServerID());
		bos.writeAsAscii(" " + fileID);
		bos.writeAsAscii(" " + chunkNo);
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		
		return Arrays.copyOf(bos.getBytes(), bos.size());
	}
	
	
}
