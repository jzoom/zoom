package com.jzoom.zoom.common.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;


/**
 * 提供io支持
 * 
 * @author jzoom
 *
 */
public class Io {

	public static String readString(File file, String charset) throws IOException {
		return readString(new FileInputStream(file), charset);
	}

	public static String readString(InputStream is, String charset) throws IOException {
		return readString(new InputStreamReader(is, charset));
	}

	public static String readString(Reader reader) throws IOException {
		return readString(new BufferedReader(reader));
	}

	public static String readString(BufferedReader reader) throws IOException {
		try {
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} finally {
			close(reader);
		}
	}

	public static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
			}
		}
	}

	public static int read(FileInputStream inputStream, byte[] buffer) throws IOException {
		return read(inputStream, buffer,0,buffer.length);
	}

	public static int read(final InputStream input, final byte[] buffer, final int offset, final int length)
			throws IOException {
		if (length < 0) {
			throw new IllegalArgumentException("Length must not be negative: " + length);
		}
		int remaining = length;
		while (remaining > 0) {
			final int location = length - remaining;
			final int count = input.read(buffer, offset + location, remaining);
			if (EOF == count) { // EOF
				break;
			}
			remaining -= count;
		}
		return length - remaining;
	}

	
	public static final int EOF = -1;


	public static void writeAndClose(OutputStream outputStream, byte[] bytes) throws IOException {
		try {
			outputStream.write(bytes);;
			outputStream.flush();
		}finally {
			Io.close(outputStream);
		}
		
	}
}
