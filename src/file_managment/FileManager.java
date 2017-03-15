package file_managment;

import backup_service.protocols.ChannelManager;
import com.sun.xml.internal.fastinfoset.algorithm.IntEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.algorithm.IntegerEncodingAlgorithm;
import utils.Debug;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FileManager {

    private int chunk_size_bytes = 64000;
    private String main_path;
    private MessageDigest hasher;
    private ChannelManager channels;
    private HashMap<String,HashMap<Integer,HashSet<Integer>>> mapeador = new HashMap<String,HashMap<Integer,HashSet<Integer>>>();

    public static void main(String[] args){
        Debug.log("BOAS");
    }

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

    public boolean save_chunck(byte[] chunkData, byte[] fileID, int chunk_num, int senderID) throws IOException {

        this.save_file_chunk_data(fileID,chunk_num,senderID);

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

        return true;
    }

    private void save_file_chunk_data(byte[] fileID, int chunk_num, int senderID){

        String path_to_data = this.main_path + File.separator + fileID.toString() + File.separator + "data";

        if(mapeador.containsKey(fileID.toString())){//Ja existe um mapeamento
            HashMap<Integer,HashSet<Integer>> temp = mapeador.get(fileID.toString());
            mapeador.put(fileID.toString(),helper_func(temp,chunk_num,senderID));
        } else {//Verificar se o ficheiro existe
            if(Files.exists(Paths.get(path_to_data))){//ler o ficheiro e serializar
                HashMap<Integer,HashSet<Integer>> hmap = read_from_data_file(Paths.get(path_to_data));
                mapeador.put(fileID.toString(),helper_func(hmap,chunk_num,senderID));
            } else {
                HashSet<Integer> hset = new HashSet<Integer>();
                hset.add(senderID);
                HashMap<Integer,HashSet<Integer>> hmap = new HashMap<Integer,HashSet<Integer>>();
                hmap.put(chunk_num,hset);
                mapeador.put(fileID.toString(),hmap);
            }

        }
    }

    private HashMap<Integer,HashSet<Integer>> helper_func(HashMap<Integer,HashSet<Integer>> hmap, int chunk_num, int senderID){

        if(hmap.containsKey(chunk_num)){
            hmap.get(chunk_num).add(senderID);
        } else {
            HashSet<Integer> hset = new HashSet<Integer>();
            hset.add(senderID);
            hmap.put(chunk_num,hset);
        }
        return hmap;
    }

    private HashMap<Integer,HashSet<Integer>> read_from_data_file(Path path) {

        HashMap<Integer, HashSet<Integer>> res = null;
        try
        {
            FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            res = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException e)
        {
            Debug.log("ERROR"," Failed to open file");
        } catch (ClassNotFoundException e) {
            Debug.log("ERROR"," Class not found at reading HashMap");
        }
        return res;
    }

    private void write_to_data_file(){

        for (HashMap.Entry<String,HashMap<Integer,HashSet<Integer>>> entry : mapeador.entrySet()) {
            String file_id = entry.getKey();
            HashMap<Integer,HashSet<Integer>> hmap = entry.getValue();
            String path = this.main_path + File.separator + file_id + File.separator + "data";

            try {
                FileOutputStream fos = new FileOutputStream(Paths.get(path).toFile());
                fos.write(("").getBytes());
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(hmap);
                oos.close();
                fos.close();
            }catch(IOException e)
            {
                Debug.log("ERROR"," Could not write file data");
            }
        }
    }
}
