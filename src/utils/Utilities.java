package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utilities {
	
	public final static byte[] CRLF = new byte[]{13,10};
	
	public static String getLine(ByteArrayInputStream bis){
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int end = -2;
		int n = 0; 
		
		while(true){
			if(bis.available() <= 0)
				break;
			byte r = (byte) bis.read();
			
			bos.write(r);
			if(r == Utilities.CRLF[0])
				end=n;
			if(r == Utilities.CRLF[1])
				if(end == n - 1){
					try {
						bos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return new String(bos.toByteArray()).trim();
				}
			n++;
		}
		try {
			bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static void write_slave(String p, Serializable obj) {

		Path path = Paths.get(p);

		try {

			if(!Files.exists(path))
				Files.createFile(path);
			FileOutputStream fos = new FileOutputStream(path.toFile());
			fos.write(("").getBytes());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.close();

		}catch(IOException e)
		{
			Debug.log("WRITE_SLAVE"," Could not write file data");
			e.printStackTrace();
		}

	}

	public static void write_slave(String p, Serializable obj,int disk_size) {

		Path path = Paths.get(p);

		try {

			if(!Files.exists(path))
				Files.createFile(path);
			FileOutputStream fos = new FileOutputStream(path.toFile());
			fos.write(("").getBytes());
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.writeInt(disk_size);
			oos.close();
			fos.close();

		}catch(IOException e)
		{
			Debug.log("WRITE_SLAVE"," Could not write file data");
			e.printStackTrace();
		}

	}
	
}
