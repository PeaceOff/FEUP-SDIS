package file_managment;

import utils.Debug;
import utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class My_files {

    private ArrayList<Metadata> my_files = new ArrayList<Metadata>();
    private ArrayList<FileInProgress> in_progress = new ArrayList<FileInProgress>();
    private ArrayList<DeletedFile> deleted_files = new ArrayList<DeletedFile>();

    public void add_file_in_progress(String name,int c,int repD){

        for(int i = 0; i < in_progress.size(); i++)
            if(in_progress.get(i).get_name().equals(name))
                return;

        in_progress.add(new FileInProgress(name,c,repD));
    }

    public void add_file_in_progress(String name,int repD){

        for(int i = 0; i < in_progress.size(); i++)
            if(in_progress.get(i).get_name().equals(name))
                return;

        in_progress.add(new FileInProgress(name,repD));
    }

    public void remove_file_in_progress(String name){

        for(int i = 0; i < in_progress.size(); i++){
            if(in_progress.get(i).get_name().equals(name)) {
                in_progress.remove(i);
                break;
            }
        }
    }

    public ArrayList<FileInProgress> get_files_in_progress() {
        return in_progress;
    }

    public ArrayList<Metadata> getMy_files() {
        return my_files;
    }

    public void setMy_files(ArrayList<Metadata> my_files) {
        this.my_files = my_files;
    }

    public void add_chunk_rep(String fileID,int chunk_no, HashSet<Integer> rep){

        for(int i = 0; i < my_files.size(); i++){
            if(my_files.get(i).fileID.equals(fileID))
                my_files.get(i).append_reps(chunk_no,rep);
        }

    }

    public void add(Metadata data){

        this.my_files.add(data);

    }

    public boolean is_my_file(String fileID){

        for(int i = 0; i < my_files.size(); i++){

            if(my_files.get(i).fileID.equals(fileID))
                return true;
        }

        return false;
    }

    @Override
    public String toString() {

        String res = "";

        for (Metadata m : my_files) {
            res += '\t' + m.toString() + '\n';
        }

        return res;
    }

    public String delete_my_file(String path_to_file) {
        String res  = "";

        for(int i = 0; i < my_files.size(); i++){

            if(my_files.get(i).getFile_path().equals(path_to_file)) {
                res = my_files.get(i).fileID;
                my_files.remove(i);
                return res;
            }
        }

        return null;
    }

    public String get_file_id(String file_path) {

        for(int i = 0 ; i < my_files.size(); i++){
            if(my_files.get(i).getFile_path().equals(file_path)){
                return my_files.get(i).fileID;
            }
        }

        return null;
    }

    public void setIn_progress(ArrayList<FileInProgress> in_progress) {
        this.in_progress = in_progress;
    }

    public FileInProgress get_next_file_in_progress() {

        if(in_progress.size() > 0)
            return in_progress.get(0);

        return null;
    }

    public ArrayList<FileInProgress> getIn_progress() {
        return in_progress;
    }

    public void add_deleted_file_entry(String file_id, HashSet<Integer> total_peers) {

        for(int i = 0 ; i < deleted_files.size(); i++){
            if(deleted_files.get(i).getFile_id().equals(file_id)){
                return;
            }
        }

        deleted_files.add(new DeletedFile(file_id,total_peers));

    }

    public void remove_deleted_file_entry(String file_id) {

        for(int i = 0 ; i < deleted_files.size(); i++){
            if(deleted_files.get(i).getFile_id().equals(file_id)){
                deleted_files.remove(i);
                return;
            }
        }
    }

    public HashSet<Integer> getPeers(String file_id) {

        for(int i = 0 ; i < my_files.size(); i++){
            if(my_files.get(i).fileID.equals(file_id)){
                return my_files.get(i).get_total_peers();
            }
        }

        return null;
    }

    public boolean remove_peer(String file_id, int peer) {

        for(int i = 0 ; i < deleted_files.size(); i++){
            if(deleted_files.get(i).getFile_id().equals(file_id)){
                return deleted_files.get(i).remove_peer(peer);
            }
        }

        return true;
    }

    public ArrayList<DeletedFile> getDeleted_files() {
        return deleted_files;
    }

    public void setDeleted_files(ArrayList<DeletedFile> deleted_files) {
        this.deleted_files = deleted_files;
    }

    public DeletedFile get_first_deleted_file() {

        if(deleted_files.size() > 0)
            return deleted_files.get(0);

        return null;
    }

    public boolean peer_deleted_chunk(String fileID, int chunk_no, int senderID) {

        for(int i = 0 ; i < my_files.size(); i++){
            if(my_files.get(i).fileID.equals(fileID)){
                return my_files.get(i).peer_deleted_chunk(chunk_no,senderID);
            }
        }

        return false;
    }

    public void peer_stored_chunk(String fileID, int chunk_num, int senderID) {

        for(int i = 0 ; i < my_files.size(); i++){
            if(my_files.get(i).fileID.equals(fileID)){
                my_files.get(i).peer_stored_chunk(chunk_num,senderID);
                return;
            }
        }

    }
}
