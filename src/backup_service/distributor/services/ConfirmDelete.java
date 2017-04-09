package backup_service.distributor.services;

import backup_service.distributor.IDistribute;
import backup_service.protocols.ChannelManager;
import backup_service.protocols.HeaderInfo;
import file_managment.FileManager;
import utils.Debug;

public class ConfirmDelete implements IDistribute{

    FileManager fileManager;

    public ConfirmDelete(FileManager mngr){
        fileManager = mngr;
    }

    @Override
    public boolean distribute(String line) {
        HeaderInfo header = new HeaderInfo(line);


        if(!header.version.equals(ChannelManager.getVersion()))
            return false;


        if(header.senderID == ChannelManager.getServerID())
            return false;

        if(fileManager.remove_peer(header.fileID,header.senderID)) {
            Debug.log("ConfirmDelete","All files responded bye bye!");
            fileManager.remove_deleted_file_entry(header.fileID);
        }

        Debug.log(1,"CONFDEL","Data:" + header);
        return true;
    }

    @Override
    public void distribute(byte[] data) {
    }
}
