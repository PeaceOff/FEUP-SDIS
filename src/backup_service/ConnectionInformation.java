package backup_service;

public class ConnectionInformation {
	private int port;
	private String ip;
	
	public ConnectionInformation(String information){
		String[] args = information.split(":");
		ip = args[0];
		port = Integer.parseInt(args[1].trim());
	}
	
	public ConnectionInformation(String ip, int port){
		this.port = port;
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public String getIP() {
		return ip;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ip + ":" + port;
	}
}
