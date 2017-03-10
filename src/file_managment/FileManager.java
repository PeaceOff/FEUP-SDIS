package file_managment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FileManager {

    Path path;
    byte[] file_id;

    byte[][] chunks;
    int n_chunks;

    private int chunk_size_bytes = 64000;
    private String tempDir_path;
    private MessageDigest hasher;

    public FileManager() throws IOException, NoSuchAlgorithmException {

        this.tempDir_path = System.getProperty("java.io.tmpdir");
        this.hasher = MessageDigest.getInstance("SHA-256");
    }

    public FileInChunks get_chunks_from_file(String path) throws IOException {

        Path file = Paths.get(path);
        BasicFileAttributes metadata = Files.readAttributes(file, BasicFileAttributes.class);

        byte[] file_bytes = Files.readAllBytes(file);

        int len = file_bytes.length;
        int n_chunks = 0;
        byte[][] chunks = new byte[0][this.chunk_size_bytes];

        for (int i = 0; i < len - this.chunk_size_bytes + 1; i += this.chunk_size_bytes)
            chunks[n_chunks++] = Arrays.copyOfRange(file_bytes, i, i + this.chunk_size_bytes);

        //Ultima chunk, ja garante que se for multiplo a ultima ficara com tamanho 0
        chunks[n_chunks] = Arrays.copyOfRange(file_bytes, len - len % this.chunk_size_bytes, len);

        byte[] file_id = this.generate_file_id(metadata,new File(path).getName());

        return new FileInChunks(chunks,n_chunks,file_id);
    }

    private byte[] generate_file_id(BasicFileAttributes metadata, String file_name) throws UnsupportedEncodingException {

        String identifier = file_name + metadata.creationTime().toString() + metadata.lastModifiedTime().toString() + metadata.size()+toString();
        this.hasher.update(identifier.getBytes("ASCII"));
        return hasher.digest();
    }

    public boolean save_chunck(byte[] chunkData, byte[] fileID, int chunk_num) throws IOException {

        String directory = this.tempDir_path + File.separator + fileID.toString();
        Path folder_path = Paths.get(directory);
        if(!Files.exists(folder_path))//Ja existe a pasta
            new File(directory).mkdir();//talvez mkdir()

        Path chunk_path = Paths.get(directory + File.separator + Integer.toString(chunk_num));
        if(Files.exists(chunk_path))
            return false;

        Files.write(chunk_path, chunkData);

        return true;
    }
}
