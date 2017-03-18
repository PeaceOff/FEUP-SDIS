package file_managment;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Debug;

public class FilePartitioned {
	
	private String fileID;
	private HashMap<Integer, byte[]> chunks = new HashMap<Integer, byte[]>();
	private int totalChunks = -1;
	
	public FilePartitioned(String fileID){
		this.fileID = fileID;
	}
	
	public void addChunk(String fileID, int chunkNo, byte[] data){
		if(!this.fileID.equals(fileID)){
			Debug.log("#########FILE ID DIFFER!!!");
			return;
		}
		if(data.length < FileManager.chunk_size_bytes){
			totalChunks = chunkNo;
			Debug.log("#########LAST CHUNK!!!");
		}
		Debug.log("#########ADDED!!!" + chunkNo);
		chunks.put(chunkNo, data);
		Debug.log("#########---"+ chunks.entrySet().toString());
	}
	
	public int totalChunks(){
		return totalChunks;
	}
	
	public void deleteChunk(int chunkNo){
		if(chunks.containsKey(chunkNo))
			chunks.remove(chunkNo);
	}
	
	public byte[] getChunk(int chunkNo){
		Debug.log(chunks.entrySet().toString());
		if(chunks.containsKey(chunkNo)){
			Debug.log("##########      HAS CHUNK ENTRY!!!");
			return chunks.get(chunkNo);
		}
		
		return null;
	}
	
	
}
