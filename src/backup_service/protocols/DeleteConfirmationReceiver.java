package backup_service.protocols;

import backup_service.distributor.services.ConfirmDelete;
import backup_service.distributor.services.RestoreChunk;
import file_managment.FileManager;
import utils.Debug;
import utils.Utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class DeleteConfirmationReceiver extends Thread {

	private FileManager fileManager;
	private int port;

	public DeleteConfirmationReceiver(int port, FileManager fm) throws IOException{
		this.port = port;
		fileManager = fm;
	}
	
	@Override
	public void run() {
		ConfirmDelete ch = new ConfirmDelete(fileManager);
		byte[] message = new byte[64*1024];
		int size=0;
		
		while(true){
		
			try {

				ServerSocket svSocket = new ServerSocket(port);
				Debug.log(0,"DeleteConfirmationReceiver","Waiting for connection");
				Socket s = svSocket.accept();
				Debug.log(0,"DeleteConfirmationReceiver","Connected!");
				svSocket.close();
				size = 0;
				
				byte[] buf = new byte[64*1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				for(int st; (st=s.getInputStream().read(buf)) != -1; )
				{
				  baos.write(buf, 0, st);
				} 
				message = baos.toByteArray();
				
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Debug.log(0,"DeleteConfirmationReceiver","Message Size:" + size);
			//message = Arrays.copyOfRange(buf,0,size);
			 
			ByteArrayInputStream bis = new ByteArrayInputStream(message);
			
			Debug.log(0,"DeleteConfirmationReceiver","CONFDEL Received");
			
			String line = Utilities.getLine(bis);
			while( line==null || !line.equals("")){
				if(!ch.distribute(line)){
					return;
				}
				line = Utilities.getLine(bis);
			}
		}
	}
}
