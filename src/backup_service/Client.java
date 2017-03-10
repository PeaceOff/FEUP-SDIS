package backup_service;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



public class Client {

    String remote_object_name;
    String operation;
    String operand_1;//Nome do ficheiro onde se vai operar ou espaço a reclamar (KByte)
    String operand_2;//Replication degree (apenas no comando BACKUP!)

    public static void main(String args[]){
        Client c = new Client();

        c.remote_object_name = args[0];
        c.operation = args[1];
        if(args.length > 2)
            c.operand_1 = args[2];//Pode ser null;
        if(args.length > 3) {
            if(!c.operation.equals("BACKUP")){
                System.out.println("Operand_2 is only used in the BACKUP call");
                System.exit(0);
            }
            c.operand_2 = args[3]; //Pode ser null;
        }

        try {
            Registry reg = LocateRegistry.getRegistry("localhost");
            IBackup peer = (IBackup) reg.lookup(c.remote_object_name);

            c.parseCommand(peer);

        } catch (Exception e) {
            System.err.println("Client exception :" + e.toString());
            e.printStackTrace();
        }
    }

    public void parseCommand(IBackup peer) throws RemoteException{

        switch (this.operation){//Estao na ordem que os docentes sugerem implementar.
            case "BACKUP":
                peer.backup(this.operand_1,this.operand_2);
                break;
            case "DELETE":
                peer.delete(this.operand_1);
                break;
            case "RESTORE":
                peer.restore(this.operand_1);
                break;
            case "RECLAIM":
                peer.reclaim(this.operand_1);
                break;
            case "STATE":
                String res = peer.state();
                System.out.println("Local Service State Info\n" + res);
                break;
            default:
                break;
        }

        return;
    }

}