package backup_service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBackup extends Remote{

    void backup(String file_path, int rep_degree) throws RemoteException;

    void delete(String file_id) throws RemoteException;

    void restore(String file_id) throws RemoteException;

    void reclaim(String space) throws RemoteException;

    String state() throws RemoteException;
}
