package com.zdnst.chameleon.httputil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


public class HttpUtil {

	public static final String HTTP_GET = "HTTP_GET";
	public static final String HTTP_POST = "HTTP_POST";
	public static final String HTTP_PUT = "HTTP_PUT";
	public static final String HTTP_DELETE = "HTTP_DELETE";
	public static final boolean ENCRYP_AND_GZIP = false;
	// public static final String ENCODING = "GB2312";
	public static final String UTF8_ENCODING = "UTF-8";
	public static final String GB2312_ENCODING = "GB2312";
	private static int HTTP_CONNECTION_TIMEOUT = 40000;
	private static int SOCKET_TIMEOUT = 30000;
	public static String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAOKfbHS+q59D" +
			"22ZHw8VL/ewC0OVsyJRSUEnErPEACK7qW6cg3eeP/Q5Qjj+HQdchfzx1wQbsgGlCHu+tprqzzkcbFTnfa9nD8Gewu" +
			"2VT9TObekpEwCwQMEEcQYgUAxD78loBKUgINFFrFaSn8I3Vad5ymEcVlo7ETkKu1/lS+g6LAgMBAAECgYB1Myee1MDjE+/" +
			"SXIjlbyB5vxcTn4e4FT3KeLlLxc230CHoM/ou+GtRzN1UA3pMbNllhix2jTb3uKdRIshYRAcIC9mygR9grBRyzE8uqqe+vOjaG" +
			"grbaEVS/74M+WjmwNraOmh1VD06ghGkg12xf2iTLHrXSrIUtLPXEuD8SaBY0QJBAPIKUJ1FquGJxKpw+uF3CYB4+HitTpA1ax1IIp" +
			"wXobdZhgQMn//A2ie9ecxBrUNSYb/WPy7zrZATLlPUR5eYZokCQQDvsXikTbuv9bmn3lZm/mgwDSmu2co9A+2L2xR8v0E9nu4wUTxuwyGV" +
			"x1iKKui4Q9XB5uZQv4LCu4fWl3LHdEdzAkA32xGHedBZhAWSn8gFyAa1UzVkA/qhZPJ3K3JxOzLisRIwVQmHZ+XwTdWRwYZOhvBv6O1j1HA1U3f" +
			"ZeJ+c6FqhAkBCYZ4NstF169Gc4gB/yZlFJYATwpE10K6q+uNzoOwKisdgbj8UVcopVun4aeXFklPSvYWvezpVf+Yg0hShlFxtAkASy5aYxNF+TG1r" +
			"c4sfZxZnjHmzevMqVeugcaXiMwa1ShApirpf0zPVHiJbC51ihQF94eFRZIS/Dce6a5qifncY";
	public static HttpURLConnection openHttpURLConnection(String httpUrl)
			throws MalformedURLException, IOException {
		URL url = new URL(httpUrl);
		URLConnection conn = url.openConnection();


		conn.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
		return (HttpURLConnection) conn;
	}

	public static HttpClient getHttpClient() throws MalformedURLException,
			IOException {
		HttpParams httpParameters = new BasicHttpParams();
		// httpParameters.setParameter(HttpMethod, value)
		// Set the timeout in milliseconds until a connection is established.
		// httpParameters.setParameter(name, value)
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				HTTP_CONNECTION_TIMEOUT);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
		return new DefaultHttpClient(httpParameters);
	}

	public static HttpClient getHttpClient(int connectionTimeout,
			int socketTimeout) throws MalformedURLException, IOException {
		HttpParams httpParameters = new BasicHttpParams();
		// httpParameters.setParameter(HttpMethod, value)
		// Set the timeout in milliseconds until a connection is established.
		// httpParameters.setParameter(name, value)
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				connectionTimeout);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, socketTimeout);
		return new DefaultHttpClient(httpParameters);
	}

	public static String doHttp(String url, String request, String httpMethod,
			boolean encrypAndGzip) throws Exception{
		String result = "";
		HttpClient httpClient = HttpUtil.getHttpClient();
		result = realDo(url, request, httpMethod, encrypAndGzip, result,
					httpClient);
		return result;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			Log.d("cube", "httpͨѶ�쳣��" + ex.getMessage());
//			return result;
//			// DialogUtil.showDialog(context, "�����쳣", ex.getMessage());
//		}

	}

	public static String doHttp(int connectionTimeout, int socketTimeout,
			String url, String request, String httpMethod, String encoding, boolean encrypAndGzip) throws Exception {

//		try {
			HttpClient httpClient = HttpUtil.getHttpClient(connectionTimeout, socketTimeout);
			return realDo(url, request, httpMethod, encrypAndGzip, encoding, httpClient);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			Log.d("cube", "httpͨѶ�쳣��" + ex.getMessage());
//			return "";
//		}

	}

	private static String realDo(String url, String request, String httpMethod, boolean encrypAndGzip, String encoding, HttpClient httpClient)
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		String result = "";
		HttpUriRequest httpRequest;
		HttpResponse response;
		AbstractHttpEntity httpEntity;

		if (HTTP_POST.equals(httpMethod)) {
			httpRequest = new HttpPost(url);
			if (encrypAndGzip) {
				byte[] zip_data = DESEncryptAndGzipUtil.encryptAndGzip(request);
				httpEntity = new InputStreamEntity(new ByteArrayInputStream(
						zip_data), zip_data.length);
			} else {
				boolean isPost = isPostRequest(request);
				boolean isForm = isFormRequest(request);
				boolean isUpload = isUploadRequest(request);
				if (isForm) {
					List<NameValuePair> nvps = new ArrayList<NameValuePair>();

					String actualRequest = request.substring(5);
					// String actualRequestBeenSign = actualRequest.replace(";",
					// "&");

					StringBuffer cc = new StringBuffer();

					String[] pairs = actualRequest.split(";");

					for (int i = 0; i < pairs.length; i++) {
						String[] keyvalue = pairs[i].split("=");
						String value = "";
						if (keyvalue.length > 1) {
							value = keyvalue[1];
						}
						Log.d("cube", keyvalue[0] + "=" + value);
						if (i != 0) {
							cc.append("&" + keyvalue[0] + "="
									+ URLEncoder.encode(value, encoding));
							// cc.append("&" + keyvalue[0] + "=" + value);
						} else {
							cc.append(keyvalue[0] + "="
									+ URLEncoder.encode(value, encoding));
							// cc.append(keyvalue[0] + "=" + value);
						}
						nvps.add(new BasicNameValuePair(keyvalue[0], value));
					}

					
					String sign = RSAUtil.sign(cc.toString(),PRIVATE_KEY, encoding);

					nvps.add(new BasicNameValuePair("sign", sign));
					Log.d("cube", "sign=" + sign);

					((HttpPost) httpRequest)
							.setEntity(new UrlEncodedFormEntity(nvps, encoding));
				} else if (isUpload) {
//					MultipartEntity mulEntity= new 
//					String actualRequest = request.substring(7);
//					String[] pairs = actualRequest.split(";");
//					
//					HttpEntity mpEntity = null;
//					for (int i = 0; i < pairs.length; i++) {
//						String[] keyvalue = pairs[i].split("=");
//						String value = "";
//						if (keyvalue.length > 1) {
//							value = keyvalue[1];
//						}
//						Log.d("cube", keyvalue[0] + "=" + value);
//						if (i != 0) {
//							mpEntity = new StringEntity(value, encoding);
//						} else {
//							mpEntity = new FileEntity(new File(value), "binary/octet-stream");
//						}
//					}
//					((HttpPost) httpRequest).setEntity(mpEntity);
//					StringBuffer cc = new StringBuffer();
					String actualRequest = request.substring(7);
					String[] pairs = actualRequest.split(";");
					MultipartEntity mpEntity = new MultipartEntity();
					for (int i = 0; i < pairs.length; i++) {
						String[] keyvalue = pairs[i].split("=");
						String value = "";
						ContentBody contentBody = null;
						if (keyvalue.length > 1) {
							value = keyvalue[1];
						}
						Log.d("cube", keyvalue[0] + "=" + value);
						if (i != 0) {
							contentBody = new StringBody(value,
									Charset.forName(encoding));
						} else {
							contentBody = new FileBody(new File(value));
						}

						mpEntity.addPart(keyvalue[0], contentBody);
					}

					((HttpPost) httpRequest).setEntity(mpEntity);
					
				} else if(isPost){
						List<NameValuePair> nvps = new ArrayList<NameValuePair>();

						String actualRequest = request.substring(5);
						// String actualRequestBeenSign = actualRequest.replace(";",
						// "&");

						StringBuffer cc = new StringBuffer();

						String[] pairs = actualRequest.split(";");


						for (int i = 0; i < pairs.length; i++) {
							String[] keyvalue = pairs[i].split("=");
							String value = "";
							if (keyvalue.length > 1) {
								value = keyvalue[1];
							}
							Log.d("cube", keyvalue[0] + "=" + value);
							if (i != 0) {
								cc.append("&" + keyvalue[0] + "="
										+ URLEncoder.encode(value, encoding));
								// cc.append("&" + keyvalue[0] + "=" + value);
							} else {
								cc.append(keyvalue[0] + "="
										+ URLEncoder.encode(value, encoding));
								// cc.append(keyvalue[0] + "=" + value);
							}
							nvps.add(new BasicNameValuePair(keyvalue[0], value));
						}

//						String sign = RSAUtil.sign(cc.toString(),
//								CubeConstants.PRIVATE_KEY, encoding);
//						Log.d("cube", "sign��" + sign);
//
//						nvps.add(new BasicNameValuePair("sign", sign));
//						Log.d("cube", "sign=" + sign);

						((HttpPost) httpRequest)
								.setEntity(new UrlEncodedFormEntity(nvps, encoding));
				} else {
					httpEntity = new StringEntity(request, encoding);
//					httpEntity.setContentType("text/xml");
//					httpEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json")); 
					((HttpPost) httpRequest).setEntity(httpEntity);
				}

			}
		} else if(httpMethod .equals(HTTP_DELETE)){
			httpRequest = new HttpDelete(url);
		} else if(httpMethod .equals(HTTP_PUT)){
			httpRequest = new HttpPut(url);
		} else{
			httpRequest = new HttpGet(url);
		}
		response = httpClient.execute(httpRequest);
		int statusCode = response.getStatusLine().getStatusCode();
		switch(statusCode){
		case HttpStatus.SC_OK:
			if (encrypAndGzip) {
				byte[] resResult = EntityUtils.toByteArray(response.getEntity());
				result = DESEncryptAndGzipUtil.unzipAndDecrypt(resResult);
			} else {
				HttpEntity httpResponseEntity = response.getEntity();
				InputStream is = httpResponseEntity.getContent();
				result = ConvertUtil.convertStreamToString(is, encoding);
			}
			break;
		case HttpStatus.SC_BAD_REQUEST:
			result = String.valueOf(statusCode);
		case HttpStatus.SC_INTERNAL_SERVER_ERROR:
			result = String.valueOf(statusCode);
		case HttpStatus.SC_NOT_FOUND:
			result = String.valueOf(statusCode);
		default :
			break;
		}
		return result;
	}

	private static boolean isPostRequest(String request){
		if (request.toUpperCase().startsWith("POST:")) {
			return true;
		} else {
			return false;
		}
	}
	private static boolean isFormRequest(String request) {
		if (request.toUpperCase().startsWith("FORM:")) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isUploadRequest(String request) {
		if (request.toUpperCase().startsWith("UPLOAD:")) {
			return true;
		} else {
			return false;
		}
	}

	public static String doWrapedHttp(Context context, String... params) throws Exception{
		String result = "";
		if (params.length < 2) {
			throw new IllegalArgumentException("���������ȷ");
		}

		String url = params[0].trim();
		Log.d("cube", "url=" + url);
		String request = params[1];
		Log.d("cube", "����=" + request);

		String encoding = UTF8_ENCODING;
		try {
			encoding = params[2];
		} catch (Exception ex) {
			// do nothing.
		}

		String httpMethod = HTTP_POST;
		try {
			httpMethod = params[3];
		} catch (Exception ex) {
			// do nothing.
		}

		int connectionTimeout = HTTP_CONNECTION_TIMEOUT;
		try {
			connectionTimeout = Integer.valueOf(params[4]);
		} catch (Exception ex) {
			// do nothing.
		}

		int socketTimeout = SOCKET_TIMEOUT;
		try {
			connectionTimeout = Integer.valueOf(params[5]);
		} catch (Exception ex) {
			// do nothing.
		}

		result = doHttp(connectionTimeout, socketTimeout, url, request, httpMethod, encoding, ENCRYP_AND_GZIP);

		Log.d("cube", "��Ӧ======" + result);
		return result;
	}

	public static boolean hasInternet(Context context) {

		if (context == null) {
			return false;
		}

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}

		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
			return true;
		}

		return true;

	}
}
