package file_managment;


import java.io.Serializable;

public class FileInProgress implements Serializable{

    private static final long serialVersionUID = 3010733700916473151L;
    private boolean is_path;
    private String path_or_file_id;
    private int chunk_no;
    private int rep_degree;

    public FileInProgress(String fileID,int c_no,int rd){
        this.is_path = false;
        this.path_or_file_id = fileID;
        this.chunk_no = c_no;
        this.rep_degree = rd;
    }

    public FileInProgress(String file_path,int rd){
        this.is_path = true;
        this.path_or_file_id = file_path;
        this.chunk_no = -1;
        this.rep_degree = rd;
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

    public int getRep_degree() { return rep_degree;}
}
