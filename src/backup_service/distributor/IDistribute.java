package backup_service.distributor;

public interface IDistribute {
	public boolean distribute(String line);	
	public void distribute(byte[] data);	
}
