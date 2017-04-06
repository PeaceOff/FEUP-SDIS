package backup_service.distributor;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Distributor {
	
	public ConcurrentHashMap<String, IDistribute> distributor = new ConcurrentHashMap<String, IDistribute>();
	public ConcurrentHashMap<String, ArrayList<IMessageListener>> msgLstnr = new ConcurrentHashMap<String, ArrayList<IMessageListener>>();
	
	public void addDistributor(String name, IDistribute service){
		
		distributor.put(name, service);
		
	}
	
	public void removeListener(String name, IMessageListener lstnr){
		
		if(msgLstnr.containsKey(name)){
			msgLstnr.get(name).remove(lstnr);
		}
		
	}
	
	public synchronized void addListener(String name, IMessageListener service){
			
		ArrayList<IMessageListener> array = null;
		if(msgLstnr.containsKey(name))
			array = msgLstnr.get(name);
		else{
			array = new ArrayList<IMessageListener>();
			msgLstnr.put(name, array);
		}
		
		array.add(service);
		
	}
	
	public boolean routeLine(String line){
		if(line.length() == 0)
			return true;
		
		String cmd = line.split(" ")[0].trim();
		if(distributor.containsKey(cmd)){
			if(!distributor.get(cmd).distribute(line)){
				return false;
			}
		}
		if(msgLstnr.containsKey(cmd)){
			for(IMessageListener lstnr : msgLstnr.get(cmd)){
				lstnr.messageReceived(line);
			}
		}
		return true;
	}
	
	public void routeBytes(byte[] bytes){
		if(distributor.containsKey("DATA")){
			distributor.get("DATA").distribute(bytes);
		}
	}
	
	
	
}
