package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
	
}
