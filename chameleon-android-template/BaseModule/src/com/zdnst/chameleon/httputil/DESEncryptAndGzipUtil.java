package com.zdnst.chameleon.httputil;


import android.util.Log;

public class DESEncryptAndGzipUtil {

	public static final String ENCODING = HttpUtil.UTF8_ENCODING;

	public static byte[] encryptAndGzip(String source) {
		try {
			byte[] encoded = DESEncrypt.getInstance().encryptMode(
					source.getBytes(ENCODING));
			String encodedString = DESEncrypt.getInstance().encode(encoded);

			byte[] zip_data = GZipUtil.gzip(encodedString.getBytes(ENCODING));

			return zip_data;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String unzipAndDecrypt(byte[] source) {
		try {
			byte[] unzip_data = GZipUtil.unzip(source);

			byte[] srcBytes = DESEncrypt.getInstance().decryptMode(
					DESEncrypt.getInstance().decode(
							new String(unzip_data, ENCODING)));
			return new String(srcBytes, ENCODING);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static String encrypt(String source) {
		try {
			byte[] encoded = DESEncrypt.getInstance().encryptMode(
					source.getBytes(ENCODING));
			return DESEncrypt.getInstance().encode(encoded);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
