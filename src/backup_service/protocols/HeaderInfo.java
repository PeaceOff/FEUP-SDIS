package backup_service.protocols;

public class HeaderInfo {
	
	public String version = "None";
	public int senderID = -1;
	public String fileID = "None";
	public int chunkNo = -1;
	public int replicationDeg = -1;
	
	public HeaderInfo(String header){
		
		header = header.replace("\\s+", " ");
		String[] res = header.split(" ");
		
		int nArgs = MessageConstructor.getArgumentNumber(res[0]);
		
		if(nArgs < 1) return;
		version = res[1];
		if(nArgs < 2) return;
		senderID = Integer.parseInt(res[2]);
		if(nArgs < 3) return;
		fileID = res[3];
		if(nArgs < 4) return;
		chunkNo = Integer.parseInt(res[4]);
		if(nArgs < 5) return;
		replicationDeg = Integer.parseInt(res[5]);
		
	}
	
	public HeaderInfo(String version, int senderID, String fileID, int chunkNo, int replicationDeg){
		this.version = version;
		this.senderID = senderID;
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.replicationDeg = replicationDeg;
	}
	
	public HeaderInfo clone() {
		return new HeaderInfo(version, senderID, fileID, chunkNo, replicationDeg);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("V:");
		sb.append(version);
		sb.append("#");

		sb.append("[ ");
		sb.append(chunkNo);
		sb.append(" ]");

		sb.append("S_ID:");
		sb.append(senderID);
		sb.append(" | ");
		
		sb.append("F_ID:");
		sb.append(fileID);
		sb.append(" | ");

		sb.append("RepDeg:");
		sb.append(replicationDeg);
		sb.append(";");
		
		
		return sb.toString();
	}
	
	
}
