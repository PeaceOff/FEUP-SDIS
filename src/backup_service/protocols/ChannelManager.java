package backup_service.protocols;

import java.io.IOException;

public class ChannelManager {
	
	private static final int MC = 0;
	private static final int MDB = 1;
	private static final int MDR = 2;
    private static String protocol_version;
    private static int server_id;
    
	private Subprotocol[] connections = new Subprotocol[3];
	
	public ChannelManager(String[] args){
		protocol_version = args[3]; //Verificar!
    	server_id = Integer.parseInt(args[4]); //Verificar!
    	
		try {
			connections[MC]  = new MC(args[0], this);
	    	connections[MDB] = new MDB(args[1], this);
	    	connections[MDR] = new MDB(args[2], this);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
    	startConnections();
	}
    
	private void startConnections(){
    	for(int i = 0; i<connections.length;i++ ){
    		if(connections[i] != null)
    			connections[i].start();
    	}
    }
	
	public static String getVersion() {
		return protocol_version;
	}
	
	public static int getServerID() {
		return server_id;
	}
	
	public MC getMC(){
		return (MC)connections[MC];
	}
	
	public MDB getMDB(){
		return (MDB)connections[MDB];
	}

	public MDR getMDR(){
		return (MDR)connections[MDR];
	}
	
}
