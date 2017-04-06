package file_managment;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Debug;

public class ChunkManager {

	private ArrayList<String> acceptedIDs = new ArrayList<String>();
	
	private HashMap<String, FilePartitioned> files = new HashMap<String,FilePartitioned>();
	
	public FilePartitioned ListenToFile(String fileID){
		FilePartitioned part = new FilePartitioned(fileID);
		Debug.log("FILEID SIZE" + fileID.length());
		acceptedIDs.add(fileID);
		files.put(fileID, part);
		
		return part;
	}
	
	public void StopListen(String fileID){
		acceptedIDs.remove(fileID);
	}
	
	public FilePartitioned getFilePartitioned(String fileID){
		
		if(!files.containsKey(fileID))
			return null;
		
		return files.get(fileID);
	}
	
	public synchronized void AddChunk(String fileID, int chunkNo, byte[] data){
		if(!acceptedIDs.contains(fileID)){
			Debug.log("#########Couldn't Find FILEID!!!");
			return;
		}
		
		FilePartitioned tempFile = null;
		
		if(files.containsKey(fileID)){
			tempFile = files.get(fileID);
			Debug.log("#########CONTAINS FILEID!!!");
		}else{
			tempFile = new FilePartitioned(fileID);
			files.put(fileID, tempFile);
			Debug.log("#########ADDED FILEID!!!");
		}
		
		tempFile.addChunk(fileID, chunkNo, data);
		Debug.log("#########ADDED CHUNK!!!!!");
	}
	
}
