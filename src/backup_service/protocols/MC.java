package backup_service.protocols;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import utils.Debug;
import utils.Utilities;

public class MC extends Subprotocol{
	
	public MC(String version, int server_id, String ipNport) throws IOException {
		super(version, server_id, ipNport);
	}
	
	public void sendSTORED(String fileID, int chunkNo) throws IOException{
		ByteOutputStream bos = new ByteOutputStream();
	
		bos.writeAsAscii("STORED");
	
		bos.writeAsAscii(" " + Subprotocol.getVersion());
		bos.writeAsAscii(" " + Subprotocol.getServer_id());
		bos.writeAsAscii(" " + fileID);
		bos.writeAsAscii(" " + chunkNo);
		bos.write(Utilities.CRLF);
		bos.write(Utilities.CRLF);
		Debug.log(1,"SIZE SENT!!!:",""+bos.size());
		this.getConnection().sendData(Arrays.copyOf(bos.getBytes(), bos.size()));
		
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
	}
	
	private BackupHeader readHeaders(ByteArrayInputStream bis){
		
		BackupHeader header = null;
		String line = null;
		int i = 0;
		
		while(line == null || !line.equals("")){
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
