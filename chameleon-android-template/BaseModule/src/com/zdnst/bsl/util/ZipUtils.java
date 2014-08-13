package com.zdnst.bsl.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {
	/**
	 * 解压文件
	 * 
	 * @param module
	 * @param listener
	 * @throws Exception 
	 */
	public static boolean unZipFile(String zipFile, String targetDir) throws Exception {
			File archive = new File(zipFile);
			try {
				ZipFile zipfile = new ZipFile(archive);
				for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
					ZipEntry entry = (ZipEntry) e.nextElement();
					unzipEntry(zipfile, entry, targetDir);
				}
				return true;
			} catch (Exception e) {
			}
			return false;
	}
	
	private static void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir)
			throws IOException {

		if (entry.isDirectory()) {
			createDir(new File(outputDir, entry.getName()));
			return;
		}

		File outputFile = new File(outputDir, entry.getName());
//		String a = IOUtils.toString(entry.getName().getBytes("GBK"), "UTF-8");
//		Log.d("mytag", "entry name:" + a);
		if (!outputFile.getParentFile().exists()) {
			createDir(outputFile.getParentFile());
		}

//		Log.v("uzip", "Extracting: " + entry);
		BufferedInputStream inputStream = new BufferedInputStream(
				zipfile.getInputStream(entry));
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(outputFile));

		try {
			
			IOUtils.copy(inputStream, outputStream);
		} finally {
			outputStream.close();
			inputStream.close();
		}
	}
	
	private static void createDir(File dir) {
		if (dir.exists()) {
			return;
		}
//		Log.v("uzip", "Creating dir " + dir.getName());
		if (!dir.mkdirs()) {
			throw new RuntimeException("Can not create dir " + dir);
		}
	
	}	
}
