package file_managment;

import backup_service.protocols.ChannelManager;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileManager {

    private int chunk_size_bytes = 64000;
    private String main_path;
    private MessageDigest hasher;
    private ChannelManager channels;

    public FileManager(ChannelManager c) throws IOException, NoSuchAlgorithmException {
        this.channels = c;
        this.main_path = System.getProperty("java.class.path") + File.separator + "backup";
        Path path = Paths.get(this.main_path);
        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Directory already exists!");
        }
        this.hasher = MessageDigest.getInstance("SHA-256");
    }

    public File_Chunk get_chunks_from_file(String path) throws IOException {

        Path file = Paths.get(path);
        BasicFileAttributes metadata = Files.readAttributes(file, BasicFileAttributes.class);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file.toFile()));


        int len = (int)file.toFile().length();
        byte[] file_id = this.generate_file_id(metadata,file.toFile().getName());
        int n_chunks = 0;
        byte[][] chunks = new byte[0][this.chunk_size_bytes];

        for (int i = 0; i < len - this.chunk_size_bytes + 1; i += this.chunk_size_bytes) {
            //TODO enviar para o canal apropriado a mensagem de putchunk
            reader.read(chunks[n_chunks++], i, i + this.chunk_size_bytes);
        }

        //Ultima chunk, ja garante que se for multiplo a ultima ficara com tamanho 0
        reader.read(chunks[n_chunks],len-len % this.chunk_size_bytes, len);
        reader.close();
        return new File_Chunk(chunks[0],n_chunks,file_id);
    }

    private byte[] generate_file_id(BasicFileAttributes metadata, String file_name) throws UnsupportedEncodingException {

        String identifier = file_name + metadata.creationTime().toString() + metadata.lastModifiedTime().toString() + metadata.size()+toString();
        this.hasher.update(identifier.getBytes("ASCII"));
        return hasher.digest();
    }

    public boolean save_chunck(byte[] chunkData, byte[] fileID, int chunk_num) throws IOException {

        String directory = this.main_path + File.separator + fileID.toString();
        Path folder_path = Paths.get(directory);
        if(!Files.exists(folder_path))//Ja existe a pasta
            Files.createDirectory(Paths.get(directory));

        Path chunk_path = Paths.get(directory + File.separator + Integer.toString(chunk_num));
        if(Files.exists(chunk_path))
            return false;

        Files.write(chunk_path, chunkData);

        return true;
    }
}
