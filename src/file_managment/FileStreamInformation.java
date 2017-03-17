package file_managment;

import java.io.BufferedInputStream;

public class FileStreamInformation {

	private BufferedInputStream stream;
	private String sfileID = "";
	public FileStreamInformation(String fileID, BufferedInputStream stream){

		this.sfileID = fileID;
		this.stream = stream;

		System.out.println("FinalFileID" + sfileID);
		
	}
	
	public String getFileID() {
		return sfileID;
	}

	public BufferedInputStream getStream() {
		return stream;
	}
}
