package org.yooz.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	public static String readFormInputStream (InputStream input) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte [] arr = new byte[1024];
		int len ;
		while((len=input.read(arr))!=-1) {
			out.write(arr, 0, len);
		}
		String result = out.toString();
		out.close();
		input.close();
		return result;
	}
}
