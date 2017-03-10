
public class Client {

    String peer_access_point;
    String operation;
    String operand_1;//Nome do ficheiro onde se vai operar ou espaço a reclamar (KByte)
    String operand_2;//Replication degree (apenas no comando BACKUP!)

    public static void main(String args[]){
        Client c = new Client();

        c.peer_access_point = args[0];
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
    }

    public void parseCommand(){

        switch (this.operation){//Estao na ordem que os docentes sugerem implementar.
            case "BACKUP":
                break;
            case "DELETE":
                break;
            case "RESTORE":
                break;
            case "RECLAIM":
                break;
            case "STATE":
                break;
            default:
                break;
        }

        return;
    }

}