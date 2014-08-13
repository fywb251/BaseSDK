package com.zdnst.chameleon.httputil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileCopeTool {
	/**
	 * 
	 */
	private  Context c;

	public FileCopeTool(Context c) {
		this.c = c;
	}

	public String readerFile(String path, String name) {
		String result = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File s = new File(path + "/" + name);
			try {
				FileInputStream in = new FileInputStream(s);
				int lenght = in.available();
				byte[] buffer = new byte[lenght];
				in.read(buffer);
				result = EncodingUtils.getString(buffer, "UTF-8");

			} catch (Exception e) {
			}
		}
		return result;
	}

	public boolean writeToJsonFile(String fileName, String path, String json) {
		Boolean flag = false;
		File file = null;
		OutputStream output = null;
		try {
			file = new File(path);
			if (!file.exists()) {
				file.mkdirs(); 
			}
			file = new File(path + fileName + ".json");
			output = new FileOutputStream(file);
			output.write(json.getBytes());
			output.flush();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	
	public boolean isfileExist(String path, String name) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File s = new File(path + "/" + name);
			if (s.exists()) {
				return true;
			} else {
				return false;
			}

		}
		return false;

	}

	public void deleteFile(String path) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File s = new File(path);
			s.delete();

		}
	}

	public static void deleteFolder(String path) {
		File f = new File(path);
		if (f.exists()) {
			if (f.isDirectory()) {
				String[] fileList = f.list();
				if(fileList==null){
					return;
				}
				for (int i = 0; i < fileList.length; i++) {
					String name = path + File.separator + fileList[i];
					File ff = new File(name);
					if (ff.isDirectory()) {
						deleteFolder(name);
					} else {
						ff.delete();
					}
				}
				f.delete();
				
			} else {
				System.out.println("���ļ��в���һ��Ŀ¼");
			}
		} else {
			System.out.println("�����ڸ��ļ���");
		}
	}

	public String getFromAssets(String fileName) {
		String result = "";
		try {
			InputStream in = c.getResources().getAssets().open(fileName);
			int lenght = in.available();
			byte[] buffer = new byte[lenght];
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "ENCODING");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static boolean isHaveSDCard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public void createFile(String path) {
		String s = Environment.getExternalStorageDirectory().getPath();
		if (isHaveSDCard()) {
			System.out.println("path===" + s);
			File f = new File(s + "/" + path);
			if (!f.exists()) {
				f.mkdirs();

			}
		}
	}

	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} finally {
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	public void copyOneFileToSDCard(String filePath, String dir, String fileName)
			throws IOException {
		InputStream is = c.getAssets().open(filePath);
		BufferedInputStream inBuff = null;
		inBuff = new BufferedInputStream(is);

		byte[] buffer = new byte[1024];
		if (isHaveSDCard()) {
			File f = new File(dir);
			if (!f.exists()) {
				f.mkdirs();
			}

			FileOutputStream fos = new FileOutputStream(dir + fileName);
			int len = inBuff.read(buffer);
			while (len > 0) {
				fos.write(buffer, 0, len);
				len = inBuff.read(buffer);
			}
			fos.close();
			inBuff.close();

		}
	}

	public void CopyAssets(String assetDir, String dir) {
		String[] files;
		try {
			files = c.getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		File mWorkingPath = new File(dir);
		if (!mWorkingPath.exists()) {
			if (!mWorkingPath.mkdirs()) {
			}
		}

		for (int i = 0; i < files.length; i++) {
			try {
				String fileName = files[i];
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						CopyAssets(fileName, dir + fileName + "/");
					} else {
						CopyAssets(assetDir + "/" + fileName, dir + "/"
								+ fileName + "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists()) {
					outFile.delete();
				}
				InputStream in = null;
				if (0 != assetDir.length()) {
					in = c.getAssets().open(assetDir + "/" + fileName);
				} else {
					in = c.getAssets().open(fileName);
				}
				OutputStream out = new FileOutputStream(outFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static int writeString(String file, String str, int mode) {
		java.io.File outputFile = new java.io.File(file);
		if (outputFile.exists() && mode == 0) {
			return -1;
		}
		java.io.PrintWriter output;
		try {
			output = new java.io.PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return -2;
		}
		output.print(str);
		output.close();
		return 0;
	}

	public static String readString(String file) {
		String inputStr = "";
		java.io.File inputFile = new java.io.File(file);
		java.util.Scanner input;
		try {
			input = new java.util.Scanner(inputFile);
			while (input.hasNext()) {
				inputStr += input.next();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		input.close();
		return inputStr;
	}

	
    public static byte[] readBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }  
  
    public static void writeBytes(String filePath,byte[] bfile) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){  
                dir.mkdirs();  
            }  
            file = new File(filePath);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
	
	
	public void encrypt(String path, String encAlgKey) {
		String inputStr = FileCopeTool.readString(path);
		byte[] outputBytes = null;
		try {
			
			outputBytes=SymEncrypt.encrypt(inputStr, encAlgKey);
			FileCopeTool.writeBytes(path, outputBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void decrypt(String path, String strKey) {
		byte[] inputBytes = FileCopeTool.readBytes(path);
		String outputStr = null;
		try {
			outputStr=SymEncrypt.decrypt(inputBytes,strKey);
			FileCopeTool.writeString(path, outputStr,1);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if(outputStr==null){
			return;
		}

	}
	
	
	public String[] getAssectFilePath(String identifier){
		String[] files = null;
		try {
			files = c.getResources().getAssets().list("image/snapshot/"+identifier);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}
}
