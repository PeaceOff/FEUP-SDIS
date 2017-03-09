
public class Server {

    public int id;

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

    }

}