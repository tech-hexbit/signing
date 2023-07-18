package com.sign;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class GZip {

	static final Logger log = Logger.getLogger(GZip.class.getName());

	static final String SOURCE_FILE_PATH = "src/main/resources/request.json";
	static final String DEST_FILE_PATH = "src/main/resources/request_gzip.txt";

	public static void main(String[] args) {

		GZip gZipFile = new GZip();
		gZipFile.gzipFile();
	}

	public void gzipFile() {

		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fileOutputStream = new FileOutputStream(DEST_FILE_PATH);

			GZIPOutputStream gzipOuputStream = new GZIPOutputStream(fileOutputStream);

			FileInputStream fileInput = new FileInputStream(SOURCE_FILE_PATH);

			int bytes_read;

			while ((bytes_read = fileInput.read(buffer)) > 0) {
				gzipOuputStream.write(buffer, 0, bytes_read);
			}

			fileInput.close();

			gzipOuputStream.finish();
			gzipOuputStream.close();

			System.out.println("The file was compressed successfully!");

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
