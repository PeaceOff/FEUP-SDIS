package file_managment;

import javafx.util.Pair;
import utils.Debug;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Mapeador  {

    private HashMap<String,HashMap<Integer,Pair<Integer,HashSet<Integer>>>> mapeador = new HashMap<>();
    private String main_path;

    public Mapeador (String path) {
        this.main_path = path;
    }


    public void add_entry(String path_to_data, byte[] fileID, int chunk_num, int senderID, int replication_degree) {//Adicionar uma entrada ao hashmap

        if(mapeador.containsKey(fileID.toString())){//Ja existe um mapeamento
            HashMap<Integer,Pair<Integer,HashSet<Integer>>> temp = mapeador.get(fileID.toString());
            mapeador.put(fileID.toString(),helper_func(temp,chunk_num,senderID,replication_degree));
        } else {//Verificar se o ficheiro existe
            if(Files.exists(Paths.get(path_to_data))){//ler o ficheiro e serializar
                HashMap<Integer,Pair<Integer,HashSet<Integer>>> hmap = read_from_data_file(Paths.get(path_to_data));
                mapeador.put(fileID.toString(),helper_func(hmap,chunk_num,senderID,replication_degree));
            } else {
                HashSet<Integer> hset = new HashSet<Integer>();
                hset.add(senderID);
                Pair<Integer,HashSet<Integer>> pair = new Pair<Integer,HashSet<Integer>>(replication_degree,hset);
                HashMap<Integer,Pair<Integer,HashSet<Integer>>> hmap = new HashMap<Integer,Pair<Integer,HashSet<Integer>>>();
                hmap.put(chunk_num,pair);
                mapeador.put(fileID.toString(),hmap);
            }
        }
    }

    private HashMap<Integer, Pair<Integer, HashSet<Integer>>> helper_func(HashMap<Integer,Pair<Integer,HashSet<Integer>>> hmap, int chunk_num, int senderID, int replication_degree){

        if(hmap.containsKey(chunk_num)){
            hmap.get(chunk_num).getValue().add(senderID);
        } else {
            HashSet<Integer> hset = new HashSet<Integer>();
            hset.add(senderID);
            Pair<Integer,HashSet<Integer>> pair = new Pair<Integer,HashSet<Integer>>(replication_degree,hset);
            hmap.put(chunk_num,pair);
        }
        return hmap;
    }

    private HashMap<Integer,Pair<Integer,HashSet<Integer>>> read_from_data_file(Path path) {

        HashMap<Integer, Pair<Integer,HashSet<Integer>>> res = null;
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

        for (Map.Entry<String, HashMap<Integer, Pair<Integer, HashSet<Integer>>>> entry : mapeador.entrySet()) {
            String file_id = entry.getKey();
            HashMap<Integer, Pair<Integer, HashSet<Integer>>> hmap = entry.getValue();
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

    private File_Chunk get_chunk_to_delete(){//Retorna a chunk que tem mais replicações na rede
        //HashMap<String,HashMap<Integer,Pair<Integer,HashSet<Integer>>>>

        File_Chunk res = new File_Chunk(0,null);
        int maior = Integer.MIN_VALUE;
        for (HashMap.Entry<String,HashMap<Integer,Pair<Integer,HashSet<Integer>>>> entry : mapeador.entrySet()) {
            String file_id = entry.getKey();
            HashMap<Integer, Pair<Integer,HashSet<Integer>>> hmap = entry.getValue();
            for(Map.Entry<Integer, Pair<Integer, HashSet<Integer>>> entry1 : hmap.entrySet()) {
                int chunk_n = entry1.getKey();
                Pair<Integer, HashSet<Integer>> peers = entry1.getValue();
                int dif = peers.getKey() - peers.getValue().size();
                if(dif >= maior){
                    maior = dif;
                    res.setFile_id(file_id.getBytes());
                    res.setN_chunk(chunk_n);
                }
            }
        }
        return res;
    }
}
