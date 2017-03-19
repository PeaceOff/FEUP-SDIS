package file_managment;

import utils.Debug;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FileManager {
	
    private int disk_size = 1280000;//KB
    public static final int chunk_size_bytes = 64000;
    private String main_path;
    private Mapper mapper;

    private ArrayList<Metadata> my_files = new ArrayList<Metadata>();//Ficheiros que eu enviei para backup
    
    private ChunkManager chunkManager = new ChunkManager();

    public FileManager(String folderName) throws IOException, NoSuchAlgorithmException {
    	
        this.main_path = System.getProperty("java.class.path") + File.separator + folderName;
        this.mapper = new Mapper(this.main_path);
        Path path = Paths.get(this.main_path);

        if(Files.exists(path)){//Ja existe vamos ler o my_files
            load_my_files();
            return;
        }

        try {//Senao vamos entao criar o directorio
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Directory already exists!");
        }
    }

    public void save_my_files(){

        String path = main_path + File.separator + "my_files";

        mapper.write_slave(path,my_files);

    }

    public void load_my_files(){

        String path_to_file = main_path + File.separator + "my_files";
        Path path = Paths.get(path_to_file);

        if(!Files.exists(path))
            return;

        try
        {
            FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            my_files = (ArrayList<Metadata>)ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException e)
        {
            Debug.log("ERROR"," Failed to open my_files file");
        } catch (ClassNotFoundException e) {
            Debug.log("ERROR"," Class not found at reading ArrayList");
        }

    }

    public FileOutputStream createFile(String fileName) throws IOException{
    	String directory = System.getProperty("java.class.path") + File.separator + "_RESTORED";
    	
    	createDirecotry(directory);
    	
    	File file = new File(directory + File.separator + fileName);
    	
    	FileOutputStream of = new FileOutputStream(file);
    	return of;
    }
    
    public void createDirecotry(String directory) throws IOException{
    	Path path = Paths.get(directory);
    	if(!Files.exists(Paths.get(directory))){
    		Files.createDirectory(path);
    	}
    }
    
    public FileStreamInformation get_chunks_from_file(String path) throws IOException {
    	
        Path file = Paths.get(path);
        BasicFileAttributes metadata = Files.readAttributes(file, BasicFileAttributes.class);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file.toFile()));
        
        int len = (int)file.toFile().length();
        Metadata file_data = new Metadata(file.toFile().getName(),metadata);
        this.my_files.add(file_data);

        return new FileStreamInformation(file_data.fileID, reader);
    }

    private boolean is_my_file(String fileID){

        for(int i = 0; i < my_files.size(); i++){

            if(my_files.get(i).fileID.equals(fileID))
                return true;
        }

        return false;
    }

    public boolean save_chunk(byte[] chunkData, String fileID, int chunk_num, int senderID, int replication_degree) throws IOException {

        if(!enough_disk_space())
            return false;

        if(is_my_file(fileID))
            return false;

        String directory = this.main_path + File.separator + fileID.toString();
        Path folder_path = Paths.get(directory);
        if(!Files.exists(folder_path))//Ainda nao existe a pasta
            Files.createDirectory(Paths.get(directory));

        Path chunk_path = Paths.get(directory + File.separator + Integer.toString(chunk_num));
        if(Files.exists(chunk_path)) {//Ja se guardou a chunk
            return true;
        }

        Files.write(chunk_path, chunkData);

        this.save_file_chunk_data(fileID,chunk_num,senderID,replication_degree);

        return true;
    }

    public void save_file_chunk_data(String fileID, int chunk_num, int senderID,int replication_degree){
    	
    	if(is_my_file(fileID))
    		return;
    	
        String path_to_data = this.main_path + File.separator + fileID.toString() + File.separator + "data";

        mapper.add_entry(path_to_data,fileID,chunk_num,senderID,replication_degree);
    }

    public byte[] get_file_chunk(String fileID, int chunk_num) {

        //TODO correr ao receber um pedido de GETCHUNK 

        String path = this.main_path + File.separator + fileID + File.separator + chunk_num;

        Path file = Paths.get(path);
        if(!Files.exists(file))
            return null;

        byte[] res = new byte[chunk_size_bytes];

        try {

            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file.toFile()));
            int size = reader.read(res,0,chunk_size_bytes);
            res = Arrays.copyOf(res, size);
            reader.close();

        } catch (IOException e) {
            Debug.log("ERROR","Couldn't open/read chunk at " + fileID + ":" + chunk_num);
        }
       
        //TODO enviar mesagem CHUNK
        return res;
    }
    
    private void recursiveDelete(File file){
    	
    	if(!file.exists())
    		return;
    	
    	if(file.isDirectory()){
    		for(File f : file.listFiles())
    			recursiveDelete(f);
    	}
    	
    	file.delete();
    	
    }
    
    public void delete_file(String fileID){

        String path = this.main_path + File.separator + fileID;

        Path folder = Paths.get(path);

        recursiveDelete(folder.toFile());
		mapper.file_removed(fileID);
    }

    public boolean delete_file_chunk(String fileID, int chunk_num){

        //TODO correr ao receber um pedido de DELETE (este é o pedido quando o ficheiro é apagado na origem mas é a mesma funcao para apagar uma chunk por forma a libertar espaço)
        String path = this.main_path + File.separator + fileID + File.separator + chunk_num;

        Path file = Paths.get(path);
        try {
            Files.deleteIfExists(file);
            mapper.chunk_removed(fileID,chunk_num);
            return true;
        } catch (IOException e) {
            Debug.log("ERROR", "Could not delete file at " + fileID + ":" + chunk_num);
        }

        return false;
    }

    public boolean peer_deleted_chunk(String fileID, int chunk_no, int senderID){

        return mapper.peer_removed_chunk(fileID,chunk_no,senderID);

    }

    private void new_disk_size(){//Limpar o disco até atingir o nome espaço desejado

        File directory = Paths.get(this.main_path).toFile();
       
    }
    
    public static long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
     
        int count = files.length;
     
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            }
            else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }
    
    private boolean enough_disk_space(){
        return (getFolderSize(Paths.get(this.main_path).toFile()) + this.chunk_size_bytes <= this.disk_size);
    }

    public int getDisk_size() {
        return disk_size;
    }

    public File setDisk_size(int disk_size) {
        this.disk_size = disk_size;
  
        return Paths.get(this.main_path).toFile();
    }

	public ChunkManager getChunkManager() {
		return chunkManager;
	}

	public Mapper getMapper() {
		// TODO Auto-generated method stub
		return mapper;
	}
    
    
}
