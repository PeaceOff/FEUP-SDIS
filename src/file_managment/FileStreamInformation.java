package file_managment;

import java.io.BufferedInputStream;

public class FileStreamInformation {
	
	private byte[] fileID;
	private BufferedInputStream stream;
	private String sfileID = "";
	public FileStreamInformation(byte[] fileID, BufferedInputStream stream){
		this.fileID = fileID;
		this.stream = stream;
		
		for(int i = 0; i < fileID.length; i++){
			sfileID+= String.format("%02X", fileID[i]);
		}
		
		System.out.println("FinalFileID" + sfileID);
		
	}
	
	public String getFileID() {
		return sfileID;
	}

	public BufferedInputStream getStream() {
		return stream;
	}
}
