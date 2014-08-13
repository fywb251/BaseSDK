package com.zdnst.chameleon.httputil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileWriterUtil {

	public static void write2SdcardRoot(String content, String fileName) {
		File sharedDir = Environment.getExternalStorageDirectory();
		sharedDir.mkdirs();
		File file = new File(sharedDir, fileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content.toString().getBytes());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean mkdirInSdcard(String dirpath) {
		dirpath = dirpath.trim();
		Log.d("cube", "dirpath:" + dirpath);
		boolean success = false;
		String sdcardPath = Environment.getExternalStorageDirectory().getPath();

		if (isSdcardExistAndWriteable()) {
			if (validate(dirpath)) {
				String[] pathArr = dirpath.split("/");
				String parentPath = sdcardPath;
				for (String path : pathArr) {
					String newDirpath = parentPath + "/" + path;
					Log.d("cube", "newDirpath to mkdir:" + newDirpath);
					File newDir = new File(newDirpath);
					if (!newDir.exists()) {
						if (!newDir.mkdirs()) {
							return false;
						}
					}
					parentPath = newDirpath;
				}
				success = true;
			}

		}
		return success;
	}

	private static boolean validate(String dirpath) {
		if (dirpath.startsWith("/") || dirpath.startsWith("//")) {
			return false;
		}
		return true;
	}

	public static boolean isSdcardExistAndWriteable() {
		boolean externalStorageAvailable = false;
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		} else {
			externalStorageAvailable = externalStorageWriteable = false;
		}

		Log.d("cube", "external storage available:" + externalStorageAvailable);
		Log.d("cube", "external storage writeable:" + externalStorageWriteable);

		if (externalStorageAvailable && externalStorageWriteable) {
			return true;
		} else {
			return false;
		}
	}

}
