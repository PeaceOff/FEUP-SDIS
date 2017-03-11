package file_managment;


public class File_Chunk {
    private byte[] chunk_data;
    private int n_chunk;
    private byte[] file_id;

    public File_Chunk(byte[] data, int n_chunk, byte[] file_id) {
        this.chunk_data = data;
        this.n_chunk = n_chunk;
        this.file_id = file_id;
    }

    public byte[] getChunk_data() {
        return chunk_data;
    }

    public int getN_chunks() {
        return n_chunk;
    }

    public byte[] getFile_id() {
        return file_id;
    }
}
