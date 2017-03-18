package file_managment;

import utils.Debug;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Metadata implements Serializable {

    private String file_name;
    private String creation_time;
    private String last_modification;
    private long size;
    private MessageDigest hasher;
    public String fileID;

    public Metadata(String file_name,BasicFileAttributes metadata)  {

        this.file_name = file_name;
        this.creation_time = metadata.creationTime().toString();
        this.last_modification = metadata.lastModifiedTime().toString();
        this.size = metadata.size();
        try {
            this.hasher = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            Debug.log("ERROR","ALgorithm does not exist!");
        }
        this.fileID = this.get_file_id();
    }

    private String get_file_id() {

        String identifier = file_name + creation_time + last_modification + size;
        try {
            this.hasher.update(identifier.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            Debug.log("ERROR","Could not find hasher identifier ASCII");
        }

        byte[] fileID = hasher.digest();
        String sfileID = "";

        for(int i = 0; i < fileID.length; i++){
            sfileID+= String.format("%02X", fileID[i]);
        }
        return sfileID;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public String getLast_modification() {
        return last_modification;
    }

    public void setLast_modification(String last_modification) {
        this.last_modification = last_modification;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public MessageDigest getHasher() {
        return hasher;
    }

    public void setHasher(MessageDigest hasher) {
        this.hasher = hasher;
    }
}
