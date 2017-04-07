package file_managment;

import utils.Debug;
import utils.Utilities;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class FileManager {
	
    private int disk_size = 1280000;//Bytes
    public static final int chunk_size_bytes = 64000;
    private String main_path;
    private Mapper mapper;
    private My_files my_files;
    
    private ChunkManager chunkManager = new ChunkManager();

    public FileManager(String folderName) throws IOException, NoSuchAlgorithmException {
    	
        this.main_path = System.getProperty("java.class.path") + File.separator + folderName;
        this.mapper = new Mapper(this.main_path);
        this.my_files = new My_files();
        Path path = Paths.get(this.main_path);

        if(Files.exists(path)){//Ja existe vamos ler o my_files
            //Se ja existir significa que o server foi abaixo e voltou
            load_information(path);
            return;
        }

        try {//Senao vamos entao criar o directorio
            Files.createDirectory(path);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Directory already exists!");
        }
    }

    public void add_chunk_rep(String fileID,int chunk_no, int rep){

        my_files.add_chunk_rep(fileID,chunk_no,rep);

    }

    private void load_information(Path path){

        load_my_files();//Vamos ler os ficheiros que fizemos backup como initiator-peer
        load_in_progress();//Vamos ver se ficamos com algum ficheiro a meio do backup
        mapper.load_my_data_files(path);//E vamos preencher a informação dos chunks que temos guardados em disco

    }

    public void add_file_in_progress(String name,int c){
        my_files.add_file_in_progress(name,c);
        save_in_progress();
    }

    public void add_file_in_progress(String name){
        my_files.add_file_in_progress(name);
        save_in_progress();
    }

    public void remove_file_in_progress(String name){

        my_files.remove_file_in_progress(name);
        save_in_progress();

    }

    public ArrayList<FileInProgress> get_files_in_progress(){

        return my_files.get_files_in_progress();

    }

    public void save_in_progress(){

        String path = main_path + File.separator + "in_progress";

        Utilities.write_slave(path,my_files.getIn_progress());

    }

    public void save_my_files(){

        String path = main_path + File.separator + "my_files";

        Utilities.write_slave(path,my_files.getMy_files(),disk_size);

    }

    public void load_in_progress() {

        //TODO se tiver algum elemento iniciar o backup

        String path_to_file = main_path + File.separator + "in_progress";
        Path path = Paths.get(path_to_file);

        if(!Files.exists(path))
            return;

        try
        {
            FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object ob = ois.readObject();

            my_files.setIn_progress((ArrayList<FileInProgress>)ob);
            ois.close();
            fis.close();
        }catch(IOException e)
        {
            Debug.log("LOAD_IN_PROGRESS"," Failed to open my_files file");
        } catch (ClassNotFoundException e) {
            Debug.log("LOAD_IN_PROGRESS"," Class not found at reading ArrayList");
        }

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
            Object ob = ois.readObject();
           
            my_files.setMy_files((ArrayList<Metadata>)ob);
            disk_size = (Integer)ois.readInt();
            ois.close();
            fis.close();
        }catch(IOException e)
        {
            Debug.log("LOAD_MY_FILES"," Failed to open my_files file");
        } catch (ClassNotFoundException e) {
            Debug.log("LOAD_MY_FILES"," Class not found at reading ArrayList");
        }

    }

    public FileOutputStream createFile(String file_path) throws IOException{

        //String directory = System.getProperty("java.class.path") + File.separator + "_RESTORED";
    	
    	//createDirectory(directory);
    	Files.deleteIfExists(Paths.get(file_path));//apagar o existente para escrever o novo

    	File file = new File(file_path);
    	
    	FileOutputStream of = new FileOutputStream(file);
    	return of;
    }

    public void delete_restored_file(String file_path) throws IOException {

        Files.deleteIfExists(Paths.get(file_path));
    }

    public void createDirectory(String directory) throws IOException{
    	Path path = Paths.get(directory);
    	if(!Files.exists(Paths.get(directory))){
    		Files.createDirectory(path);
    	}
    }
    
    public FileStreamInformation get_chunks_from_file(String path,int rep_degree) throws IOException {
    	
        Path file = Paths.get(path);
        BasicFileAttributes metadata = Files.readAttributes(file, BasicFileAttributes.class);
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file.toFile()));

        long len = file.toFile().length();
        Metadata file_data = new Metadata(file.toFile().getCanonicalPath(),metadata,rep_degree,len);
        this.my_files.add(file_data);
        save_my_files();

        return new FileStreamInformation(file_data.fileID, reader);
    }

    public boolean is_my_file(String fileID){

        return this.my_files.is_my_file(fileID);
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

    public synchronized byte[] get_file_chunk(String fileID, int chunk_num) {

        //TODO correr ao receber um pedido de GETCHUNK 

        String path = this.main_path + File.separator + fileID + File.separator + chunk_num;

        Path file = Paths.get(path);
        if(!Files.exists(file))
            return null;

        byte[] res = new byte[chunk_size_bytes];

        try { 
        	
        	if(file.toFile().length() == 0){
        		return new byte[0];
        	}
        	
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file.toFile()));
            int size = reader.read(res,0,chunk_size_bytes);
           
            res = Arrays.copyOf(res, size);
            reader.close();

        } catch (IOException e) {
            Debug.log("GET_FILE_CHUNK","Couldn't open/read chunk at " + fileID + ":" + chunk_num);
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
    
    public synchronized void delete_file(String fileID){

        String path = this.main_path + File.separator + fileID;

        Path folder = Paths.get(path);

        recursiveDelete(folder.toFile());
		mapper.file_removed(fileID);
    }

    public synchronized boolean delete_file_chunk(String fileID, int chunk_num){

        //TODO correr ao receber um pedido de DELETE (este é o pedido quando o ficheiro é apagado na origem mas é a mesma funcao para apagar uma chunk por forma a libertar espaço)
        String path = this.main_path + File.separator + fileID + File.separator + chunk_num;
        String file_path = this.main_path + File.separator + fileID;

        Path file = Paths.get(path);
        try {
            Files.deleteIfExists(file);
            mapper.chunk_removed(fileID,chunk_num);

            if(Paths.get(file_path).toFile().listFiles().length <= 1){
                recursiveDelete(Paths.get(file_path).toFile());
                mapper.file_removed(fileID);
            }
            return true;
        } catch (IOException e) {
            Debug.log("DELETE_FILE_CHUNK", "Could not delete file at " + fileID + ":" + chunk_num);
        }

        return false;
    }

    public synchronized boolean peer_deleted_chunk(String fileID, int chunk_no, int senderID){

        if(is_my_file(fileID))
            return true;

        return mapper.peer_removed_chunk(fileID,chunk_no,senderID);

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
        return (getFolderSize(Paths.get(this.main_path).toFile()) + FileManager.chunk_size_bytes <= this.disk_size);
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

    @Override
    public String toString() {

        String res = "My Files : \n";

        res += my_files.toString();

        res += "---------------------------------------------------------------\n";

        res += mapper.toString();

        res += "---------------------------------------------------------------\n";

        res += "Storage Capacity : " + this.disk_size + " | Occupied : " + getFolderSize(Paths.get(this.main_path).toFile()) + '\n';

        return res;
    }

    public String delete_my_file(String path_to_file) {

        String res  = my_files.delete_my_file(path_to_file);

        if(res != null)
            save_my_files();

        return res;
    }

    public String get_file_id(String file_path) {

        return my_files.get_file_id(file_path);

    }
}
