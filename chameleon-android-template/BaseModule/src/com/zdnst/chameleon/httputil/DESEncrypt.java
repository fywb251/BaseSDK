package com.zdnst.chameleon.httputil;

//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Administrator
 * 
 */
public class DESEncrypt {
	private static final String Algorithm = "DESede";
	public static final byte[] keyBytes = "This is a secret keynews".getBytes();

	private static DESEncrypt encrypt;

	public static DESEncrypt getInstance() {
		if (encrypt == null) {
			encrypt = new DESEncrypt();
		}
		return encrypt;
	}

	
	public byte[] encryptMode(byte[] src) {
		try {
			SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	public String decrypt(byte[] src) throws Exception {
		String srcString = new String(src, "UTF-8");
		byte[] srcBytes = decryptMode(decode(srcString));
		return new String(srcBytes, "UTF-8");
	}

	public byte[] encrypt(String src) throws Exception {
		String srcString = encode(encryptMode(src.getBytes("UTF-8")));
		return srcString.getBytes("UTF-8");
	}

	/*
	 * @ use DESede algorithm to dec the src
	 * 
	 * @ keybyte: secretkey 24 byte
	 * 
	 * @ src:the text needs to be dec
	 * 
	 * @ return the dec result
	 */
	public byte[] decryptMode(byte[] src) {
		try {
			SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
			Cipher c1 = Cipher.getInstance("DESede/ECB/PKCS5Padding");
			c1.init(Cipher.DECRYPT_MODE, deskey);
			return c1.doFinal(src);
		} catch (java.security.NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return null;
	}

	// private void writeFile(String src) throws Exception {
	// File file = new File("C://encrypt.txt");
	// FileWriter fw = new FileWriter(file);
	// fw.write(src);
	// fw.close();
	// }
	//
	// private String readFile(String path) {
	// File file = new File(path);
	// FileReader fr = null;
	// try {
	// fr = new FileReader(file);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// char[] cbuf = new char[10240];
	// try {
	// fr.read(cbuf);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// String res = new String(cbuf);
	// return res;
	// }

	public String encode(byte[] bstr) {
		// return new sun.misc.BASE64Encoder().encode(bstr);
		// return new String(Base64.encodeBase64(bstr));
		return MyBase64.encodeToString(bstr, MyBase64.DEFAULT);
	}

	public byte[] decode(String str) {
		byte[] bt = null;
		// try {
		bt = MyBase64.decode(str, MyBase64.DEFAULT);
		// bt = Base64.decodeBase64(str.getBytes("UTF-8"));
		// sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		// bt = decoder.decodeBuffer(str);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		return bt;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {

		// TripleDESEncrypt encrypt = new TripleDESEncrypt();
		// Security.addProvider(new com.sun.crypto.provider.SunJCE());
		// String keyString ="This is a secret keynews";
		// byte[] keyBytes = keyString.getBytes();
		// System.out.println("��Կ�ǣ�"+new String(keyBytes));
		// String szSrc = "AFE0AFE6-8DBA-E6CB-3240-103AC6F94766";
		// System.out.println("����ǰ���ַ�:" + szSrc);
		// byte[] encoded =
		// TripleDESEncrypt.getInstance().encryptMode(szSrc.getBytes("UTF-8"));
		// String encodedString = encrypt.encode(encoded);
		// System.out.println("���ܺ���ַ�:" + encodedString);
		//
		//
		// String urlEncodeStr = URLEncoder.encode(encodedString, "UTF-8");
		// System.out.println("UTF-9������ַ�:" + urlEncodeStr);
		//
		// try {
		// encrypt.writeFile(encodedString);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// byte[] srcBytes =
		// TripleDESEncrypt.getInstance().decryptMode(encrypt.decode(encodedString));
		// System.out.println("���ܺ���ַ�:" + (new String(srcBytes,"UTF-8")));
	}
}
