package file_managment;

import java.util.ArrayList;

public class My_files {

    public ArrayList<Metadata> my_files = new ArrayList<Metadata>();

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
}
