package file_managment;


public class File_Chunk {
    private byte[] chunk_data;
    private int n_chunk;

    public void setChunk_data(byte[] chunk_data) {
        this.chunk_data = chunk_data;
    }

    public void setN_chunk(int n_chunk) {
        this.n_chunk = n_chunk;
    }

    public void setFile_id(byte[] file_id) {
        this.file_id = file_id;
    }

    private byte[] file_id;

    public File_Chunk(byte[] data, int n_chunk, byte[] file_id) {
        this.chunk_data = data;
        this.n_chunk = n_chunk;
        this.file_id = file_id;
    }

    public File_Chunk(int n_chunk, byte[] file_id) {
        this.chunk_data = null;
        this.n_chunk = n_chunk;
        this.file_id = file_id;
    }

    public byte[] getChunk_data() {
        return chunk_data;
    }

    public int getN_chunk() {
        return n_chunk;
    }

    public byte[] getFile_id() {
        return file_id;
    }
}
