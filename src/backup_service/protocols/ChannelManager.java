package backup_service.protocols;

import java.io.IOException;

import backup_service.distributor.Distributor;

public class ChannelManager {
	
	private static final int MC = 0;
	private static final int MDB = 1;
	private static final int MDR = 2;
    private static String protocol_version;
    private static int server_id;
    
	private Subprotocol[] connections = new Subprotocol[3];
	
	public ChannelManager(String[] args, Distributor[] distributors){
		protocol_version = args[3]; //Verificar!
    	server_id = Integer.parseInt(args[4]); //Verificar!
    	
		try {
			connections[MC]  = new Subprotocol(args[0],  this,  distributors[0]);
	    	connections[MDB] = new Subprotocol(args[1],  this,  distributors[1]);
	    	connections[MDR] = new Subprotocol(args[2],  this,  distributors[2]);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
    	startConnections();
	}
    
	private void startConnections(){
    	for(int i = 0; i<connections.length;i++ )
    		if(connections[i] != null)
    			connections[i].start();
    }
	
	public static String getVersion() {
		return protocol_version;
	}
	
	public static int getServerID() {
		return server_id;
	}
	
	public Subprotocol getMC(){
		return connections[MC];
	}
	
	public Subprotocol getMDB(){
		return connections[MDB];
	}

	public Subprotocol getMDR(){
		return connections[MDR];
	}
	
}
