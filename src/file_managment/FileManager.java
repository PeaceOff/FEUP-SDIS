package file_managment;

import backup_service.protocols.ChannelManager;
import javafx.util.Pair;
import utils.Debug;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FileManager {

    private static int disk_size = 1280000;//KB
    private int chunk_size_bytes = 64000;
    private String main_path;
    private MessageDigest hasher;
    private ChannelManager channels;
    private Mapeador mapeador;
    private ArrayList<String> my_files = new ArrayList<String>();//Ficheiros que eu enviei para backup

    public static void main(String[] args){
        Debug.log("BOAS");
    }

    public FileManager(ChannelManager c) throws IOException, NoSuchAlgorithmException {
        this.channels = c;
        this.main_path = System.getProperty("java.class.path") + File.separator + "backup";
        this.mapeador = new Mapeador(this.main_path);
        Path path = Paths.get(this.main_path);
        //TODO verificar primeiro se o directory ja existe?
        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Directory already exists!");
        }
        this.hasher = MessageDigest.getInstance("SHA-256");
    }

    public void get_chunks_from_file(String path) throws IOException {

        Path file = Paths.get(path);
        BasicFileAttributes metadata = Files.readAttributes(file, BasicFileAttributes.class);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file.toFile()));

        int len = (int)file.toFile().length();
        byte[] file_id = this.generate_file_id(metadata,file.toFile().getName());
        this.my_files.add(file_id.toString());
        int n_chunks = 0;
        byte[] chunk = new byte[this.chunk_size_bytes];

        for (int i = 0; i < len - this.chunk_size_bytes + 1; i += this.chunk_size_bytes) {

            reader.read(chunk, i, i + this.chunk_size_bytes);

            File_Chunk f_chunk = new File_Chunk(chunk,n_chunks,file_id);
            //TODO fazer putchunk da File_Chunk!
            n_chunks++;
        }

        //Ultima chunk, ja garante que se for multiplo a ultima ficara com tamanho 0
        reader.read(chunk,len-len % this.chunk_size_bytes, len);
        File_Chunk f_chunk = new File_Chunk(chunk,n_chunks,file_id);
        //TODO enviar a ultima chunk
        reader.close();
    }

    private byte[] generate_file_id(BasicFileAttributes metadata, String file_name) throws UnsupportedEncodingException {

        String identifier = file_name + metadata.creationTime().toString() + metadata.lastModifiedTime().toString() + metadata.size()+toString();
        this.hasher.update(identifier.getBytes("ASCII"));
        return hasher.digest();
    }

    public boolean save_chunk(byte[] chunkData, byte[] fileID, int chunk_num, int senderID, int replication_degree) throws IOException {

        if(!enough_disk_space())
            return false;

        this.save_file_chunk_data(fileID,chunk_num,senderID,replication_degree);

        String directory = this.main_path + File.separator + fileID.toString();
        Path folder_path = Paths.get(directory);
        if(!Files.exists(folder_path))//Ainda nao existe a pasta
            Files.createDirectory(Paths.get(directory));

        Path chunk_path = Paths.get(directory + File.separator + Integer.toString(chunk_num));
        if(Files.exists(chunk_path)) {//Ja se guardou a chunk
            //TODO enviar STORED
            return false;
        }

        Files.write(chunk_path, chunkData);
        //TODO enviar STORED

        return true;
    }

    private void save_file_chunk_data(byte[] fileID, int chunk_num, int senderID,int replication_degree){

        String path_to_data = this.main_path + File.separator + fileID.toString() + File.separator + "data";

        mapeador.add_entry(path_to_data,fileID,chunk_num,senderID,replication_degree);
    }

    private boolean enough_disk_space(){
        return (Paths.get(this.main_path).toFile().length() + this.chunk_size_bytes <= this.disk_size);
    }

    public static int getDisk_size() {
        return disk_size;
    }

    public static void setDisk_size(int disk_size) {
        FileManager.disk_size = disk_size;
    }
}
