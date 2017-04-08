package file_managment;

import java.io.Serializable;
import java.util.HashSet;

public class DeletedFile implements Serializable{

    private String file_id;
    private HashSet<Integer> peers;

    public DeletedFile(String file_id, HashSet<Integer> peers) {
        this.file_id = file_id;
        this.peers = peers;
    }

    public String getFile_id() {
        return file_id;
    }

    public HashSet<Integer> getPeers() {
        return peers;
    }

    public boolean remove_peer(int peer) {//Se retornar true podemos apagar a entrada

        peers.remove(peer);

        return (peers.size() == 0);

    }

}
