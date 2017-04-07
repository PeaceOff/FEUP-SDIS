package backup_service.protocols;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


import backup_service.distributor.services.RestoreChunk;
import file_managment.FileManager;
import utils.Debug;
import utils.Utilities;

public class TCPReceiver extends Thread {

	private FileManager fileManager;
	private int port;
	
	public TCPReceiver(int port, FileManager fm) throws IOException{
		this.port = port;
		fileManager = fm;
	}
	
	@Override
	public void run() {
		RestoreChunk ch = new RestoreChunk(null,fileManager);
		byte[] message = new byte[64*1024];
		int size=0;
		
		while(true){
		
			try {
				
				ServerSocket svSocket = new ServerSocket(port);
				Debug.log(0,"TCPRECEIVER","Waiting for connection");
				Socket s = svSocket.accept();
				Debug.log(0,"TCPRECEIVER","Connected!");
				svSocket.close();
				size = 0;
				
				byte[] buf = new byte[64*1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				for(int st; (st=s.getInputStream().read(buf)) != -1; )
				{
				  baos.write(buf, 0, st);
				} 
				message = baos.toByteArray();
				
				/*
				while(true){
					conf = s.getInputStream().read(buf,size, buf.length-size);
					DataInputStream ds = new DataInputStream(s.getInputStream());
					ds.readF
					Debug.log(0,"TCPRECEIVER","Reading...");
					if(conf == -1)
						break;
					size+=conf;
				}*/
				
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Debug.log(0,"TCPRECEIVER","Message Size:" + size);
			//message = Arrays.copyOfRange(buf,0,size);
			 
			ByteArrayInputStream bis = new ByteArrayInputStream(message);
			
			Debug.log(0,"TCPRECEIVER","Chunk Received");
			
			String line = Utilities.getLine(bis);
			while( line==null || !line.equals("")){
				if(!ch.distribute(line)){
					return;
				}
				line = Utilities.getLine(bis);
			} 
			
			byte[] lastBytes = new byte[bis.available()];
			Debug.log(lastBytes.length + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
			try {
				bis.read(lastBytes);
					
				bis.close();
			} catch (IOException e) {
				Debug.log(0,"TCPRECEIVER","Could not read last bytes");
				e.printStackTrace();
			}
			
			ch.distribute(lastBytes);
			
			
		}

	}
	
	
}
