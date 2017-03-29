package file_managment;

import utils.Debug;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

public class Metadata implements Serializable {

    private String file_path;
    private String creation_time;
    private String last_modification;
    private long size;
    private int rep_degree;
    public String fileID;
    public HashMap<Integer,Integer> chunks_n_reps = new HashMap<Integer,Integer>();

    public Metadata(String file_path, BasicFileAttributes metadata, int rd, long len)  {

        this.file_path = file_path;
        this.creation_time = metadata.creationTime().toString();
        this.last_modification = metadata.lastModifiedTime().toString();
        this.size = len;
        this.fileID = this.get_file_id();
        this.rep_degree = rd;
    }

    public void append_reps(int chunk_no,int reps){
        chunks_n_reps.put(chunk_no,reps);
    }

    private String get_file_id() {

        String identifier = file_path + creation_time + last_modification + size;
        MessageDigest hasher = null;
        try {
            hasher = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Debug.log("METADATA","ALgorithm does not exist!");
            System.exit(-1);
        }
        try {
            hasher.update(identifier.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            Debug.log("GET_FILE_ID","Could not find hasher identifier ASCII");
        }

        byte[] fileID = hasher.digest();
        String sfileID = "";

        for(int i = 0; i < fileID.length; i++){
            sfileID+= String.format("%02X", fileID[i]);
        }
        return sfileID;
    }

    public String getFile_path() {
        return file_path;
    }

    public static String get_file_id(Path file) {

        BasicFileAttributes metadata = null;
        long len = file.toFile().length();

        try {
            metadata = Files.readAttributes(file, BasicFileAttributes.class);
            Metadata temp = new Metadata(file.toFile().getCanonicalPath(),metadata,0,len);
            return temp.get_file_id();
        } catch (IOException e) {
            Debug.log("METADATA","GET_FILE_ID");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        String res = "";
        res += "File Path : " + file_path + '\n';
        res += "Backup Service id : " + fileID + '\n' + "Desired Rep Degree : " + rep_degree + '\n';

        Iterator it = chunks_n_reps.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            res += "\tChunk Number : " + pair.getKey() + " | Perceived Rep Degree : " + pair.getValue() + '\n';
            it.remove();
        }
        return res;
    }
}
