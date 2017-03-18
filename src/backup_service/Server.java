package backup_service;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import backup_service.distributor.Distributor;
import backup_service.distributor.IDistribute;
import backup_service.distributor.services.*;
import backup_service.protocols.*;
import file_managment.ChunkManager;
import file_managment.FileManager;
import file_managment.FilePartitioned;
import file_managment.FileStreamInformation;
import utils.Debug;

import java.io.FileOutputStream;
import java.io.IOException;



public class Server implements IBackup{
	
    private int id;
    
    public static final int MAX_RESEND = 5;
    private ChannelManager channelManager;
    private Distributor[] distributors = new Distributor[3];
    private FileManager fileManager = null;
    private Services services = null;
    
    public Server(String[] args) throws IOException, NoSuchAlgorithmException{
    	
    	fileManager = new FileManager("Backup!"+args[4]);
    	
    	distributors[0] = new Distributor();
    	distributors[1] = new Distributor();
    	distributors[2] = new Distributor();
    	
    	channelManager = new ChannelManager(args, distributors);
    	services = new Services(channelManager);
;    	
    	distributors[0].addDistributor("STORED", new Stored(fileManager));
    	distributors[0].addDistributor("GETCHUNK", new Restore(channelManager, fileManager));
    	distributors[0].addDistributor("DELETE", new DeleteFile(fileManager));
    	distributors[0].addDistributor("REMOVED", new RemoveChunk(channelManager));
    	
    	IDistribute PUTCHUNK = new SaveChunk(channelManager, fileManager);
    	distributors[1].addDistributor("PUTCHUNK", PUTCHUNK);
    	distributors[1].addDistributor("DATA", PUTCHUNK);
    	
    	IDistribute CHUNK = new RestoreChunk(fileManager);
    	distributors[2].addDistributor("CHUNK", CHUNK);
    	distributors[2].addDistributor("DATA", CHUNK);
    	
    	//distributors[2].addDistributor("RESTORE", service);
    }

    
    public int getId() {
		return id;
	}


	public ChannelManager getChannelManager() {
		return channelManager;
	}



	public void test(){
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Subprotocol mdb = channelManager.getMDB();
    	try {
    		if(channelManager.getServerID() == 1)
    			services.sendPutChunk("THIS_IS_THE_FILE_ID_BRO_255BYTESTHIS_IS_THE_FILE_ID_BRO_255BYTES", 255, 3, new byte[]{1,2,3});
    		if(channelManager.getServerID() == 2)
    			this.channelManager.getMC().sendMessage(MessageConstructor.getGETCHUNK("THIS_IS_THE_FILE_ID_BRO_255BYTESTHIS_IS_THE_FILE_ID_BRO_255BYTES", 1));
    		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
	
	@Override
    public void backup(String file_path, int rep_degree) {
    	Debug.log("BACKUP PATH:", file_path);
    	try {
			FileStreamInformation fs = fileManager.get_chunks_from_file(file_path);
			byte[] chunkData = new byte[FileManager.chunk_size_bytes];
			
			int chunkNo = 0;
			while(true){
				
				if(fs.getStream().available() == 0){ //Was a pair
					services.sendPutChunk(fs.getFileID(), chunkNo, rep_degree, new byte[]{});
					break;
				}
				
				int size = fs.getStream().read(chunkData);
				byte[] data =  Arrays.copyOf(chunkData, size);
	
				services.sendPutChunk(fs.getFileID(), chunkNo, rep_degree, data);

				if(size < FileManager.chunk_size_bytes){
					break;
				}
				chunkNo++;
			}
			
		} catch (IOException e) {
			//RETURN MESSAGE TO THE CLIENT TELLING SOMETHING IS WRONG!
			e.printStackTrace();
		}
    	
    	
    	
    }

    @Override
    public void delete(String file_id) {
    	try {
			channelManager.getMC().sendMessage(MessageConstructor.getDELETE(file_id));
		} catch (IOException e) {
		}
    }

    @Override
    public void restore(String file_id) {
    	FileOutputStream fs;
    	
    	try {
			fs = fileManager.createFile(file_id);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
    	
    	FilePartitioned filePart = fileManager.getChunkManager().ListenToFile(file_id);
    	
    	int chunkCounter = 0;
    	while(true){
    		try {
				
    			this.channelManager.getMC().sendMessage(MessageConstructor.getGETCHUNK(file_id, chunkCounter));
				Debug.log("Sent GETCHUNK" + chunkCounter);
				Thread.sleep(2000);
				
				byte[] chunk = filePart.getChunk(chunkCounter);
				if(chunk != null){
					chunkCounter++;
					fs.write(chunk);
					if(filePart.totalChunks() > -1)
						if(chunkCounter > filePart.totalChunks())
							break;
				}else{
					Debug.log("Error Receiving CHUNK Retrying!");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	fileManager.getChunkManager().StopListen(file_id);
    	
    }

    @Override
    public void reclaim(int space) {

    }

    @Override
    public String state() {
        return null;
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

