package file_managment;

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
	
	public static final int total_disk_size = 1280000;//KB
    private int disk_size = 1280000;//KB
    public static final int chunk_size_bytes = 64000;
    private String main_path;
    private MessageDigest hasher;

    private Mapper mapper;
    private ArrayList<String> my_files = new ArrayList<String>();//Ficheiros que eu enviei para backup

    public static void main(String[] args){
        Debug.log("BOAS");
    }

    public FileManager() throws IOException, NoSuchAlgorithmException {
    	
        this.main_path = System.getProperty("java.class.path") + File.separator + "backup";
        this.mapper = new Mapper(this.main_path);
        Path path = Paths.get(this.main_path);
        //TODO verificar primeiro se o directory ja existe?
        try {
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Directory already exists!");
        }
        this.hasher = MessageDigest.getInstance("SHA-256");
    }
    
    
    public FileStreamInformation get_chunks_from_file(String path) throws IOException {

        Path file = Paths.get(path);
        BasicFileAttributes metadata = Files.readAttributes(file, BasicFileAttributes.class);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file.toFile()));
        
        int len = (int)file.toFile().length();
        byte[] file_id = this.generate_file_id(metadata,file.toFile().getName());
        this.my_files.add(file_id.toString());

        return new FileStreamInformation(file_id, reader);
        /*int n_chunks = 0;
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
        reader.close(); */
    }

    private byte[] generate_file_id(BasicFileAttributes metadata, String file_name) throws UnsupportedEncodingException {

        String identifier = file_name + metadata.creationTime().toString() + metadata.lastModifiedTime().toString() + metadata.size()+toString();
        this.hasher.update(identifier.getBytes("ASCII"));
        return hasher.digest();
    }

    public boolean save_chunk(byte[] chunkData, String fileID, int chunk_num, int senderID, int replication_degree) throws IOException {

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

    public void save_file_chunk_data(String fileID, int chunk_num, int senderID,int replication_degree){

        String path_to_data = this.main_path + File.separator + fileID.toString() + File.separator + "data";

        mapper.add_entry(path_to_data,fileID,chunk_num,senderID,replication_degree);
    }

    private byte[] get_file_chunk(String fileID, int chunk_num) throws FileNotFoundException {

        //TODO correr ao receber um pedido de GETCHUNK 

        String path = this.main_path + File.separator + fileID + File.separator + chunk_num;

        Path file = Paths.get(path);
        if(!Files.exists(file))
            return null;

        byte[] res = new byte[chunk_size_bytes];
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file.toFile()));
        try {
            reader.read(res,0,chunk_size_bytes);
            reader.close();
        } catch (IOException e) {
            Debug.log("ERROR","Couldn't read chunk at " + fileID + ":" + chunk_num);
        }
       
        //TODO enviar mesagem CHUNK
        return res;
    }

    private boolean delete_file_chunk(String fileID, int chunk_num){

        //TODO correr ao receber um pedido de DELETE (este é o pedido quando o ficheiro é apagado na origem mas é a mesma funcao para apagar uma chunk por forma a libertar espaço)
        String path = this.main_path + File.separator + fileID + File.separator + chunk_num;

        Path file = Paths.get(path);
        try {
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            Debug.log("ERROR", "Could not delete fileat " + fileID + ":" + chunk_num);
        }

        return false;
    }

    private void new_disk_size(){//Limpar o disco até atingir o nome espaço desejado

        File directory = Paths.get(this.main_path).toFile();
        while(directory.length() > this.disk_size){
            File_Chunk delete = this.mapper.get_chunk_to_delete();

            if(!this.delete_file_chunk(delete.getFile_id(),delete.getN_chunk()))
                Debug.log("ERROR", "Could not delete file! " + delete.toString());

            //TODO enviar o comando de REMOVED
        }
    }

    private boolean enough_disk_space(){
        return (Paths.get(this.main_path).toFile().length() + this.chunk_size_bytes <= this.disk_size);
    }

    public int getDisk_size() {
        return disk_size;
    }

    public void setDisk_size(int disk_size) {
        this.disk_size = disk_size;
        this.new_disk_size();
    }
}
