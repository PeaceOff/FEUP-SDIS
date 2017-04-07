package file_managment;

import utils.Debug;
import utils.Utilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Mapper {

    private HashMap<String,HashMap<Integer, ChunkInfo>> mapper = new HashMap<>();
    private String main_path;

    public Mapper(String path) {
        this.main_path = path;
    }


    public void add_entry(String path_to_data, String fileID, int chunk_num, int senderID, int replication_degree) {//Adicionar uma entrada ao hashmap

        if(mapper.containsKey(fileID)){//Ja existe um mapeamento
            HashMap<Integer, ChunkInfo> temp = mapper.get(fileID.toString());
            mapper.put(fileID,helper_func(temp,chunk_num,senderID,replication_degree));
            save(fileID);
        } else {//Verificar se o ficheiro existe
            if(Files.exists(Paths.get(path_to_data))){//ler o ficheiro e serializar
                HashMap<Integer, ChunkInfo> hmap = read_from_data_file(Paths.get(path_to_data));
                mapper.put(fileID,helper_func(hmap,chunk_num,senderID,replication_degree));
                save(fileID);
            } else {
                HashSet<Integer> hset = new HashSet<Integer>();
                hset.add(senderID);
                ChunkInfo pair = new ChunkInfo(replication_degree,hset);
                HashMap<Integer, ChunkInfo> hmap = new HashMap<Integer, ChunkInfo>();
                hmap.put(chunk_num,pair);
                mapper.put(fileID,hmap);
                save(fileID);
            }
        }
    }

    public void save(String fileID){

        write_data_file(fileID);

    }
    
    
    public int get_rep_degree(String fileID, int chunk_no){

        if(exists(fileID,chunk_no))
            return mapper.get(fileID).get(chunk_no).getRep_degree();

        return -1;

    }
    

    public int get_local_count(String fileID, int chunk_no){

        if(exists(fileID,chunk_no))
            return mapper.get(fileID).get(chunk_no).get_peer_count();

        return -1;

    }

    private boolean check_for_file(String fileID, int chunk_num) {

        String path = this.main_path + File.separator + fileID + File.separator + chunk_num;
        String folder = this.main_path + File.separator + fileID + File.separator + "data";

        if(Files.exists(Paths.get(path))){
        	Debug.log("POTATO");
            HashMap<Integer,ChunkInfo> hmap = read_from_data_file(Paths.get(folder));
            mapper.put(fileID,hmap);
            save(fileID);

        } else {
        	Debug.log("BATATO");
            return false;

        }

        return true;
    }

    public boolean exists(String fileID,int chunk_num){

        if(mapper.containsKey(fileID)) {

            if(mapper.get(fileID).containsKey(chunk_num)){

                return true;

            } else {

                return check_for_file(fileID,chunk_num);

            }

        } else {

            return check_for_file(fileID,chunk_num);

        }
    }

    private HashMap<Integer, ChunkInfo> helper_func(HashMap<Integer, ChunkInfo> hmap, int chunk_num, int senderID, int replication_degree){

        if(hmap.containsKey(chunk_num)){
            hmap.get(chunk_num).add_peer(senderID);
        } else {
            HashSet<Integer> hset = new HashSet<Integer>();
            hset.add(senderID);
            ChunkInfo pair = new ChunkInfo(replication_degree,hset);
            hmap.put(chunk_num,pair);
        }
        return hmap;
    }

    public void load_my_data_files(Path path){

        File f = path.toFile();
        File[] files = f.listFiles();
        for (File file : files) {
            if(file.isDirectory()) {
                String path_to_file = file.getPath() + File.separator + "data";
                HashMap<Integer, ChunkInfo> hmap = read_from_data_file(Paths.get(path_to_file));
                mapper.put(file.getName(), hmap);
            }
        }

    }

    private HashMap<Integer, ChunkInfo> read_from_data_file(Path path) {

        HashMap<Integer, ChunkInfo> res = null;

        try
        {
            FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            res = (HashMap<Integer, ChunkInfo>)ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException e)
        {
            Debug.log("READ_FROM_DATA_FILE"," Failed to open file");
            Debug.log(path.toString());
        } catch (ClassNotFoundException e) {
            Debug.log("READ_FROM_DATA_FILE"," Class not found at reading HashMap");
        }
        return res;
    }

    private void write_data_file(String file_id){

        String path = this.main_path + File.separator + file_id + File.separator + "data";
        
        if(!mapper.containsKey(file_id))
            return;
        
        if(!Files.exists(Paths.get(this.main_path + File.separator + file_id + File.separator))){
        	return; 
        }
        
        HashMap<Integer,ChunkInfo> hmap = mapper.get(file_id);

        Utilities.write_slave(path,hmap);

    }

    public FileChunk get_chunk_to_delete(){//Retorna a chunk que tem mais replicações na rede

        FileChunk res = new FileChunk(0,null);
        int maior = Integer.MIN_VALUE;
        
        for (HashMap.Entry<String,HashMap<Integer, ChunkInfo>> entry : mapper.entrySet()) {

            String file_id = entry.getKey();
            HashMap<Integer, ChunkInfo> hmap = entry.getValue();

            for(Map.Entry<Integer, ChunkInfo> entry1 : hmap.entrySet()) {

                int chunk_n = entry1.getKey();

                ChunkInfo peers = entry1.getValue();
                int dif = peers.getRep_degree() - peers.get_peer_count();

                if (dif >= maior) {

                    maior = dif;
                    res.setFile_id(file_id);
                    res.setN_chunk(chunk_n);
                }
            }
        }
        return res;
    }

    public void file_removed(String fileID){

        if(mapper.containsKey(fileID))
            mapper.remove(fileID);
    }

    public void chunk_removed(String fileID,int chunk_no){

        if(!mapper.containsKey(fileID))
            return;

        HashMap<Integer, ChunkInfo> hmap = mapper.get(fileID);

        if(hmap.containsKey(chunk_no))
            hmap.remove(chunk_no);
        
        if(mapper.get(fileID).size() == 0){
        	mapper.remove(fileID);
        } 
    }

    public boolean peer_removed_chunk(String fileID, int chunk_no, int senderID) {

        if(exists(fileID,chunk_no)){
        	Debug.log("EXISTS!");
        	
            return mapper.get(fileID).get(chunk_no).remove_peer(senderID);

        }
        Debug.log("DONT EXIST!");
 
        return false;
    }

    @Override
    public String toString() {

        String res = "!!Storage information!!\n";

        for (HashMap.Entry<String,HashMap<Integer, ChunkInfo>> entry : mapper.entrySet()) {

            String file_id = entry.getKey();
            HashMap<Integer, ChunkInfo> hmap = entry.getValue();

            res += "File ID : " + file_id + '\n';

            for(Map.Entry<Integer, ChunkInfo> entry1 : hmap.entrySet()) {

                int chunk_n = entry1.getKey();
                ChunkInfo peers = entry1.getValue();
                String tmp = this.main_path + File.separator + file_id + File.separator + chunk_n;
                int size = (int)(Paths.get(tmp).toFile().length()/1000);
                res += "\tChunk Number : " + chunk_n + " | Size (KB) : " + size + " | Perceived Rep Degree : " + peers.get_peer_count() + '\n';

            }
        }

        return res;
    }
}
