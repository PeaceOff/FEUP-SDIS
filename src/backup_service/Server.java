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
import file_managment.FileChunk;
import file_managment.FileManager;
import file_managment.FilePartitioned;
import file_managment.FileStreamInformation;
import utils.Debug;

import java.io.File;
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
    	services = new Services(channelManager,fileManager);
;    	
    	distributors[0].addDistributor("STORED", new Stored(fileManager));
    	distributors[0].addDistributor("GETCHUNK", new Restore(channelManager, fileManager));
    	distributors[0].addDistributor("DELETE", new DeleteFile(fileManager));
    	distributors[0].addDistributor("REMOVED", new RemoveChunk(channelManager, fileManager));
    	
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
    	/*
    	try {
    		if(channelManager.getServerID() == 1)
    			services.sendPutChunk("THIS_IS_THE_FILE_ID_BRO_255BYTESTHIS_IS_THE_FILE_ID_BRO_255BYTES", 255, 3, new byte[]{1,2,3});
    		if(channelManager.getServerID() == 2)
    			this.channelManager.getMC().sendMessage(MessageConstructor.getGETCHUNK("THIS_IS_THE_FILE_ID_BRO_255BYTESTHIS_IS_THE_FILE_ID_BRO_255BYTES", 1));
    		if(channelManager.getServerID() == 3)
    			this.channelManager.getMC().sendMessage(MessageConstructor.getREMOVED("9DF839A8DDC6641A43BA14AD8EFE1A10E7142CAB18AB8E40B7C9691021467B3B", 1));
    		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    	
    }
	
	@Override
    public void backup(String file_path, int rep_degree) {
    	Debug.log("BACKUP PATH:", file_path);
    	try {
			FileStreamInformation fs = fileManager.get_chunks_from_file(file_path,rep_degree);
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

			//Quando faz backup de um ficheiro guardar a info do my_files
			fileManager.save_my_files();
			
		} catch (IOException e) {
			//RETURN MESSAGE TO THE CLIENT TELLING SOMETHING IS WRONG!
			e.printStackTrace();
		}
    	
    	
    	
    }

    @Override
    public void delete(String path) {
    	try {

			String file_id = fileManager.delete_my_file(path);
			if(file_id == null)//Não é um ficheiro meu!
				return;
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
				Thread.sleep(600);
				
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
    	try {
			fs.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	fileManager.getChunkManager().StopListen(file_id);
    	
    }

    
    
    @Override
    public void reclaim(int space) {
    	File directory = fileManager.setDisk_size(space);
    	Debug.log("RECLAIM", "NewSPACE " + space + "KB");
    	Debug.log("RECLAIM", "DIRECTORY SIZE" + directory.length() + "KB");
    	
    	while(FileManager.getFolderSize(directory) > fileManager.getDisk_size() * 1000){
             FileChunk delete = fileManager.getMapper().get_chunk_to_delete();

             if(!fileManager.delete_file_chunk(delete.getFile_id(),delete.getN_chunk()))
                 Debug.log("ERROR", "Could not delete file! " + delete.toString());
             
             try {
				this.channelManager.getMC().sendMessage(MessageConstructor.getREMOVED(delete.getFile_id(), delete.getN_chunk()));
				Thread.sleep(500);
	            
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
     		
            
             
         }
    	
    }

    @Override
    public String state() {
		/*Retrieve local service state information
		This operation allows to observe the service state. In response to such a request,
		the peer shall send to the client the following information:

			For each file whose backup it has initiated:
				The file pathname
				The backup service id of the file
				The desired replication degree
				For each chunk of the file:
					Its id
					Its perceived replication degree

			For each chunk it stores:
				Its id
				Its size (in KBytes)
				Its perceived replication degree

			The peer's storage capacity, i.e. the maximum amount of disk space that can be
			used to store chunks, and the amount of storage (both in KBytes) used to backup the chunks.
*/
		return fileManager.toString();
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

