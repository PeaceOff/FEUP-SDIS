package file_managment;


public class FileInChunks {
    private byte[][] chunks;
    private int n_chunks;
    private byte[] file_id;

    public FileInChunks(byte[][] chunks, int n_chunks, byte[] file_id) {
        this.chunks = chunks;
        this.n_chunks = n_chunks;
        this.file_id = file_id;
    }

    public byte[][] getChunks() {
        return chunks;
    }

    public int getN_chunks() {
        return n_chunks;
    }

    public byte[] getFile_id() {
        return file_id;
    }
}
