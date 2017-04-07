package file_managment;

import utils.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class My_files {

    private ArrayList<Metadata> my_files = new ArrayList<Metadata>();
    private ArrayList<FileInProgress> in_progress = new ArrayList<FileInProgress>();

    public void add_file_in_progress(String name,int c){
        in_progress.add(new FileInProgress(name,c));
    }

    public void add_file_in_progress(String name){
        in_progress.add(new FileInProgress(name));
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

    public void add_chunk_rep(String fileID,int chunk_no, int rep){

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

    public ArrayList<FileInProgress> getIn_progress() {
        return in_progress;
    }

    public void setIn_progress(ArrayList<FileInProgress> in_progress) {
        this.in_progress = in_progress;
    }
}
