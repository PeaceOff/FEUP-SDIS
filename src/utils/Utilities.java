package utils;

import java.io.ByteArrayInputStream;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class Utilities {
	
	public final static byte[] CRLF = new byte[]{13,10};
	
	public static String getLine(ByteArrayInputStream bis){
		
		ByteOutputStream bos = new ByteOutputStream();
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
					bos.close();
					return new String(bos.getBytes()).trim();
				}
			n++;
		}
		bos.close();
		return "";
	}
	
}
