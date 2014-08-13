package com.zdnst.chameleon.httputil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class GZipUtil {
	public static byte[] gzip(byte[] data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipEntry ze = new ZipEntry("servletservice");
		ZipOutputStream zos = new ZipOutputStream(baos);
		zos.putNextEntry(ze);
		zos.write(data, 0, data.length);
		zos.close();
		byte[] zipBytes = baos.toByteArray();
		return zipBytes;
	}

	public static byte[] unzip(byte[] zipBytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(zipBytes);
		ZipInputStream zis = new ZipInputStream(bais);
		zis.getNextEntry();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final int BUFSIZ = 4096;
		byte inbuf[] = new byte[BUFSIZ];
		int n;
		try {
			while ((n = zis.read(inbuf, 0, BUFSIZ)) != -1) {
				baos.write(inbuf, 0, n);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		byte[] data = baos.toByteArray();
		zis.close();
		return data;
	}

	public static void main(String[] args) throws IOException {

		for (int i = 0; i < 1000; i++) {
			String a = "<pda>  <pda_customer_info>    <pda_id></pda_id>    <o_name>���ڷֹ�˾��ί������</o_name>    <food_love></food_love>    <t_flt_time></t_flt_time>    <headship>����</headship>    <mobile></mobile>    <t_flown_carrier></t_flown_carrier>    <address>�ϳ������С�����Ժ����      ����������</address>    <flt_date>08-8-25</flt_date>    <origin>PEK</origin>    <check_in_love></check_in_love>    <sex>Ů</sex>    <drink_love></drink_love>    <customer_tier>80</customer_tier>    <english_name>MAJUN</english_name>    <member_no></member_no>    <customer_id>520000609968    </customer_id>    <t_flown_flt_no></t_flown_flt_no>    <flt_no>3112 </flt_no>    <destination>CAN</destination>    <seat_love></seat_love>    <t_destination></t_destination>    <seatno>3D</seatno>    <chineseName>���</chineseName>    <transfer></transfer>    <ffp_tier></ffp_tier>    <certificate_id>-2300990302-T</certificate_id>    <email></email>    <corp_id>CZ  </corp_id>    <pda_flt_id>1</pda_flt_id>    <mileage>";

			byte[] abyte = a.getBytes("UTF-8");

			byte[] testbyte1 = null;

			testbyte1 = gzip(abyte);

			File file = new File("D:\\gziptest");

			FileOutputStream fos = new FileOutputStream(file);

			fos.write(testbyte1);

			fos.close();

		}

	}
}
