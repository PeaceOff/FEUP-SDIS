package backup_service;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import backup_service.protocols.*;

import java.io.IOException;



public class Server implements IBackup {
	
	private static final int MC = 0;
	private static final int MDB = 1;
	private static final int MDR = 2;
	
	
    private int id;
    
    private String protocol_version;
    
    private Subprotocol[] connections = new Subprotocol[3];
    
    public Server(String[] args) throws IOException{
    	protocol_version = args[3]; //Verificar!
    	id = Integer.parseInt(args[4]); //Verificar!
    	
    	connections[MC]=new MC(protocol_version, id, args[0]);
    	connections[MDB]=new MDB(protocol_version, id, args[1], (MC)connections[0]);
    	connections[MDR]= null;
    	startConnections();
    }
    
    private void startConnections(){
    	for(int i = 0; i<connections.length;i++ ){
    		if(connections[i] != null)
    			connections[i].start();
    	}
    }

    public static void main(String args[]){
        
        String remote_object_name = args[5];
        Server sv;
        try {
        	sv = new Server(args);
            IBackup peer = (IBackup) UnicastRemoteObject.exportObject(sv,0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(remote_object_name,peer);
            System.out.println("RMI ready!");
            sv.test();
        } catch(IOException e){
        	System.err.println("Error Initializing Server: " + e.toString());
        	e.printStackTrace();
        } catch (Exception e) {
            System.err.println("RMI failed: " + e.toString());
            e.printStackTrace();
        }
        
    }
    
    public void test(){
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	MDB mdb = (MDB)connections[MDB];
    	try {
			mdb.sendPUTCHUNK("THIS_IS_THE_FILE_ID_BRO_255BYTESTHIS_IS_THE_FILE_ID_BRO_255BYTES", 255, 3, new byte[]{1,2,3});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    @Override
    public void backup(String file_id, String rep_degree) {

    }

    @Override
    public void delete(String file_id) {

    }

    @Override
    public void restore(String file_id) {

    }

    @Override
    public void reclaim(String space) {

    }

    @Override
    public String state() {
        return null;
    }
}
//Chunk -> (fileID,chunkNum) max size : 64KBytes (64000Bytes)
/*
Backup a file
Restore a file
Delete a file
Manage local service storage
Retrieve local service state information
*/
/* Protocolos
* MC:Port MDB:Port MDR:Port Protocol_Version ServerID ServiceAccessPoint
Messages
Header
<MessageType> <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF>

1. Chunk backup
PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body> (para o MDB)
STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF> (para o MC)

2. chunk restore
GETCHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF> (para o MC)
CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body> (para o MDR)

3. file deletion
DELETE <Version> <SenderId> <FileId> <CRLF><CRLF> (para o MC)

4. space reclaiming
REMOVED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF> (para o MC)

*/

