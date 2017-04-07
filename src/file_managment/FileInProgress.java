package file_managment;


import java.io.Serializable;

public class FileInProgress implements Serializable{

    private boolean is_path;
    private String path_or_file_id;
    private int chunk_no;

    public FileInProgress(String fileID,int c_no){
        this.is_path = false;
        this.path_or_file_id = fileID;
        this.chunk_no = c_no;
    }

    public FileInProgress(String file_path){
        this.is_path = true;
        this.path_or_file_id = file_path;
        this.chunk_no = -1;
    }

    public String get_name(){
        return path_or_file_id;
    }

    public boolean is_a_path(){
        return this.is_path;
    }

    public int getChunk_no() {
        return chunk_no;
    }
}
