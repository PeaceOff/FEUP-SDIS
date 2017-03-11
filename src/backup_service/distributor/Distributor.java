package backup_service.distributor;

import java.util.HashMap;

public class Distributor {
	
	public HashMap<String, IDistribute> distributor = new HashMap<String, IDistribute>();
	
	public void addDistributor(String name, IDistribute service){
		
		distributor.put(name, service);
		
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
		return true;
	}
	
	public void routeBytes(byte[] bytes){
		if(distributor.containsKey("DATA")){
			distributor.get("DATA").distribute(bytes);
		}
	}
	
	
	
}
