package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

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

    public static boolean check_commands(String[] args) {

		if(!Pattern.matches("\\d\\.\\d",args[0])) {
			System.out.println("Version must be of type <n>.<m> where <n> and <m> are the ASCII codes of digits");
			return false;
		}

		if(!Pattern.matches("[0-9]{1,3}",args[1])){
			System.out.println("Server ID must be 1 to 3 digits");
			return false;
		}

		if(!Pattern.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}",args[3])){
			System.out.println("MC ip address is not correct! Format <IP>:<PORT>");
			return false;
		}

		if(!Pattern.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}",args[4])){
			System.out.println("MDB ip address is not correct! Format <IP>:<PORT>");
			return false;
		}

		if(!Pattern.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}",args[5])){
			System.out.println("MDR ip address is not correct! Format <IP>:<PORT>");
			return false;
		}

		return true;
    }
}
