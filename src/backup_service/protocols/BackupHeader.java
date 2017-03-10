package backup_service.protocols;

public class BackupHeader {
	
	public String version;
	public int senderID;
	public String fileID;
	public int chunkNo;
	public int replicationDeg = -1;
	public BackupHeader(String header){
		header = header.replace("\\s+", " ");
		String[] res = header.split(" ");
		version = res[1];
		senderID = Integer.parseInt(res[2]);
		fileID = res[3];
		chunkNo = Integer.parseInt(res[4]);
		
		if(res[0].equals("PUTCHUNK"))
			replicationDeg = Integer.parseInt(res[5]);
		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("V:");
		sb.append(version);
		sb.append("#");
		
		sb.append("S_ID:");
		sb.append(senderID);
		sb.append("#");
		
		sb.append("F_ID:");
		sb.append(fileID);
		sb.append("#");
		
		sb.append("C_No:");
		sb.append(chunkNo);
		sb.append("#");
		
		sb.append("RepDeg:");
		sb.append(replicationDeg);
		sb.append(";");
		
		
		return sb.toString();
	}
	
	
}
