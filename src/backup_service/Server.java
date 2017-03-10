package backup_service;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class Server implements IBackup {

    public int id;

    public Server(int id) {
        this.id = id;
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
    public static void main(String args[]){
        String multicast_channel = args[0];
        String multicast_data_backup = args[1];
        String multicast_data_restore = args[2];
        String protocol_version = args[3];
        String server_id = args[4];
        String remote_object_name = args[5];
        Server sv = new Server(Integer.parseInt(server_id));

        try {
            IBackup peer = (IBackup) UnicastRemoteObject.exportObject(sv,0);
            Registry registry = LocateRegistry.getRegistry();
            registry.bind(remote_object_name,peer);
            System.out.println("RMI ready!");
        } catch (Exception e) {
            System.err.println("RMI failed: " + e.toString());
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