package backup_service.protocols;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import utils.Utilities;

public class MessageConstructor {
	
	private static ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	public static final HashMap<String, Integer> commandArgs;
	static
	{
		commandArgs = new HashMap<String, Integer>();
		commandArgs.put("STORED", 4);
		commandArgs.put("PUTCHUNK", 5);
		commandArgs.put("GETCHUNK", 4);
		commandArgs.put("CHUNK", 4);
		commandArgs.put("DELETE", 3);
		commandArgs.put("REMOVED", 4);
	}
	
	public static int getArgumentNumber(String name){
		if(commandArgs.containsKey(name))
			return commandArgs.get(name);
		return -1;
	}
	
	public static byte[] ASCII(String s){
		return s.getBytes(StandardCharsets.US_ASCII);
	}
	
	public static byte[] getSTORED(String fileID, int chunkNo){
		bos.reset();
		try {
			
		bos.write(ASCII("STORED"));
		bos.write(ASCII(" " + ChannelManager.getVersion()));
		bos.write(ASCII(" " + ChannelManager.getServerID()));
		bos.write(ASCII(" " + fileID));
		bos.write(ASCII(" " + chunkNo));
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Arrays.copyOf(bos.toByteArray(), bos.size());
	}
	
	public static byte[] getPUTCHUNK(String fileID, int chunkNo, int replicationDeg, byte[] data) throws IOException{
		bos.reset();
		
		bos.write(ASCII("PUTCHUNK"));
		bos.write(ASCII(" " + ChannelManager.getVersion()));
		bos.write(ASCII(" " + ChannelManager.getServerID()));
		bos.write(ASCII(" " + fileID));
		bos.write(ASCII(" " + chunkNo));
		bos.write(ASCII(" " + replicationDeg));
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		bos.write(data);
		
		return Arrays.copyOf(bos.toByteArray(), bos.size());
	}
	
	public static byte[] getGETCHUNK(String fileID, int chunkNo) throws IOException{
		bos.reset();
		
		bos.write(ASCII("GETCHUNK"));
		bos.write(ASCII(" " + ChannelManager.getVersion()));
		bos.write(ASCII(" " + ChannelManager.getServerID()));
		bos.write(ASCII(" " + fileID));
		bos.write(ASCII(" " + chunkNo));
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		
		return Arrays.copyOf(bos.toByteArray(), bos.size());
	}
	
	public static byte[] getCHUNK(String fileID, int chunkNo, byte[] data) throws IOException{
		bos.reset();
		
		bos.write(ASCII("CHUNK"));
		bos.write(ASCII(" " + ChannelManager.getVersion()));
		bos.write(ASCII(" " + ChannelManager.getServerID()));
		bos.write(ASCII(" " + fileID));
		bos.write(ASCII(" " + chunkNo));
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		bos.write(data);
		
		return Arrays.copyOf(bos.toByteArray(), bos.size());
	}
	
	public static byte[] getDELETE(String fileID) throws IOException{
		bos.reset();
		
		bos.write(ASCII("DELETE"));
		bos.write(ASCII(" " + ChannelManager.getVersion()));
		bos.write(ASCII(" " + ChannelManager.getServerID()));
		bos.write(ASCII(" " + fileID));
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		
		return Arrays.copyOf(bos.toByteArray(), bos.size());
	}
	
	public static byte[] getREMOVED(String fileID, int chunkNo) throws IOException{
		bos.reset();
		
		bos.write(ASCII("REMOVED"));
		bos.write(ASCII(" " + ChannelManager.getVersion()));
		bos.write(ASCII(" " + ChannelManager.getServerID()));
		bos.write(ASCII(" " + fileID));
		bos.write(ASCII(" " + chunkNo));
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		
		return Arrays.copyOf(bos.toByteArray(), bos.size());
	}
	
	
}
