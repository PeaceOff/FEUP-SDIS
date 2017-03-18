package backup_service.distributor;

public interface IMessageListener {
	
	public void messageReceived(String line);	
	
}
