import java.rmi.Remote;

public interface IBackup extends Remote{

    void backup(String file_id, String rep_degree);

    void delete(String file_id);

    void restore(String file_id);

    void reclaim(String space);

    String state();
}
