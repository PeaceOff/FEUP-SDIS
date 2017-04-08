package backup_service;

import backup_service.protocols.ChannelManager;
import backup_service.protocols.MessageConstructor;
import file_managment.DeletedFile;
import file_managment.FileManager;
import utils.Debug;

import java.io.IOException;
import java.util.HashSet;

public class DeleteScheduler extends Thread {

    private Object state = new Object();
    public FileManager fileManager;
    public ChannelManager channelManager;

    public DeleteScheduler(ChannelManager channelManager,FileManager fileManager){
        this.channelManager = channelManager;
        this.fileManager = fileManager;
    }

    public void wake() {

        synchronized (state){
            state.notifyAll();
        }
    }

    @Override
    public void run() {

        DeletedFile d_file;
        while(true){

            Debug.log("DeleteScheduler","Working!");

            if((d_file = fileManager.get_first_deleted_file()) == null){
                synchronized (state){
                    try {
                        Debug.log("DeleteScheduler","Waiting!");
                        state.wait();
                    } catch (InterruptedException e) {
                        Debug.log("DeleteScheduler","Error waiting!");
                    }
                }
                continue;
            }

            HashSet<Integer> tmp = d_file.getPeers();
            for (Integer i : tmp) {
                Debug.log("HASHSET CONTENT ",i.toString());
            }
            
            try {
                Debug.log("DeleteScheduler","Sending Message!");
                channelManager.getMC().sendMessage(MessageConstructor.getDELETE(d_file.getFile_id()));
                Thread.sleep(5000);
            } catch (IOException e) {
                Debug.log("DeleteScheduler","Error sending delete message!");
            } catch (InterruptedException e) {
                Debug.log("DeleteScheduler","Had nightmares!");
            }

        }
    }
}
