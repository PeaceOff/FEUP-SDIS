package file_managment;

import java.io.Serializable;
import java.util.HashSet;

public class ChunkInfo implements Serializable{

    private int rep_degree;
    private HashSet<Integer> peers;

    public ChunkInfo(int rep_degree, HashSet<Integer> peers) {
        this.rep_degree = rep_degree;
        this.peers = peers;
    }

    public int getRep_degree() {
        return rep_degree;
    }

    public void setRep_degree(int rep_degree) {
        this.rep_degree = rep_degree;
    }

    public HashSet<Integer> getPeers() {
        return peers;
    }

    public void setPeers(HashSet<Integer> peers) {
        this.peers = peers;
    }

    public void add_peer(int peer){
        this.peers.add(peer);
    }

    public int get_peer_count(){
        return this.peers.size();
    }

    public void remove_peer(int peer){

        if(peers.contains(peer))
            peers.remove(peer);

    }

    public boolean can_delete(){
        return (rep_degree > (peers.size() - 1));
    }
}
