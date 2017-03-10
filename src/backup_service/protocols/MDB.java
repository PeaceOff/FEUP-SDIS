package backup_service.protocols;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import utils.Debug;
import utils.Utilities;

public class MDB extends Subprotocol {
	
	private MC mc;
	
	public MDB(String version, int server_id, String ipNport, MC mc) throws IOException {
		super(version, server_id, ipNport);
		this.mc = mc;
	}
	
	public void sendPUTCHUNK(String fileID, int chunkNo, int replicationDeg, byte[] data) throws IOException{
		ByteOutputStream bos = new ByteOutputStream();
		bos.writeAsAscii("PUTCHUNK");
		bos.writeAsAscii(" " + Subprotocol.getVersion());
		bos.writeAsAscii(" " + Subprotocol.getServer_id());
		bos.writeAsAscii(" " + fileID);
		bos.writeAsAscii(" " + chunkNo);
		bos.writeAsAscii(" " + replicationDeg);
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		bos.write(data);
		this.getConnection().sendData(bos.getBytes());
		
		bos.close();
	}
	
	@Override
	public void receiveMessage(byte[] message) {
		ByteArrayInputStream bis = new ByteArrayInputStream(message);
		
		
		BackupHeader header = readHeaders(bis);
		if(header.senderID == this.getServer_id())
			return;

		Debug.log(this.getConnection().getConnectionInfo().toString(),"Received a message!");
		Debug.log(1,this.getConnection().getConnectionInfo().toString(),"Received:" + header);
		
		//Data Try to save Data!
		
		//Send STORED!
		try {
			mc.sendSTORED(header.fileID, header.chunkNo);
		} catch (IOException e) {
			System.out.println("Error Sending STORED!!");
			e.printStackTrace();
		}
	}
	
	private BackupHeader readHeaders(ByteArrayInputStream bis){
		
		BackupHeader header = null;
		String line = null;
		int i = 0;
		
		while( line==null || !line.equals("")){
			line = Utilities.getLine(bis);
			
			switch(i){
			case 0:
				
				header = new BackupHeader(line);
				break;
			}
			
			i++;
		}
		
		return header;
	}
	
	
	
	
}
