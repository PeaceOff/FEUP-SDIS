package file_managment;


public class FileChunk {
    private byte[] chunk_data;
    private int n_chunk;
    private String _file_id;

    public void setChunk_data(byte[] chunk_data) {
        this.chunk_data = chunk_data;
    }
    
    public byte[] getChunk_data() {
        return chunk_data;
    }

    
    public void setN_chunk(int n_chunk) {
        this.n_chunk = n_chunk;
    }
    
    public void setFile_id(String file_id) {
    	_file_id = file_id;	
    }
    
    public void setFile_id(byte[] file_id) {
    	if(file_id == null)
    		return; 
    	 
    	this._file_id = "";
        
        for(int i = 0; i < file_id.length; i++)
        	this._file_id += String.format("%02X", file_id[i]);
		
    }

    public FileChunk(byte[] data, int n_chunk) {
        this.chunk_data = data;
        this.n_chunk = n_chunk;
    }

    public FileChunk(int n_chunk, byte[] file_id) {
        this.chunk_data = null;
        this.n_chunk = n_chunk;
        setFile_id(file_id);
    }

    public int getN_chunk() {
        return n_chunk;
    }

    public String getFile_id() {
        return _file_id;
    }
}
