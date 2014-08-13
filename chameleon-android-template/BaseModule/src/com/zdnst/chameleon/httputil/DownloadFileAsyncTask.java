package com.zdnst.chameleon.httputil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public abstract class DownloadFileAsyncTask extends GeneralAsynTask {

	public static final String SDCARD = "SDCARD";
	public static final String PRIVATE = "PRIVATE";
	public int EXCEPTION_MESSAGE = 0x0a;
	public Long downLoadcontentLength = 0l;
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if(msg.what==0){
				doHttpFail(null);
				stopped = true;
			}
			else{
				Exception e = (Exception) msg.getData()
						.getSerializable("exception");
				doHttpFail(e);
				stopped = true;
			}
		};
	};
	
	


	public DownloadFileAsyncTask(Context context) {
		super(context);
	}

	public DownloadFileAsyncTask(Context context, String prompt) {
		super(context, prompt);
	}

	public DownloadFileAsyncTask(Context context, ProgressDialog progressDialog) {
		super(context, progressDialog);
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		// Log.d("cube", "ANDRO_ASYNC=" + progress[0]);
	}
	

	@Override
	public void doPreExecuteBeforeDialogShow() {
//		progressDialog.setMessage("�����ļ�");
//		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progressDialog.setCancelable(true);
	}

	@Override
	public void doPreExecuteWithoutDialog() {
		super.doPreExecuteWithoutDialog();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		ThreadPlatformUtils.addDownloadTask2List(this);
	}

	@Override
	protected void doPostExecute(String result) {

	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		ThreadPlatformUtils.finishDownloadTask(this);
		if (stopped) {
			stopped = false;
			return;
		} else {

			doPostExecute(result);
		}
	}

	@Override
	protected String doInBackground(String... params) {

//		Log.d("cube", "���� url=" + params[0]);

		// ����ĵ�һ��Ϊurl���ڶ�������ΪFile��ƣ����������ѡ��Ϊд˽��(PRIVATE)����дsdcard(SDCARD)�����ĸ������ʾ��Ŀ¼��ģ����
		Message exceptionMessage = new Message();
		// exceptionMessage.what=EXCEPTION_MESSAGE;
		try {

			if (params.length < 2) {
				throw new IllegalArgumentException("���������ȷ");
			}
			HttpClient client = new DefaultHttpClient();
			for (String string : params) {
				System.out.println("params="+string);
			}
			HttpGet post = new HttpGet(params[0]);
			HttpResponse response;

			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			
			String contentDisposition = response.getFirstHeader(
					"Content-Disposition").getValue();
			Header lenHeader = response.getFirstHeader("Content-Length");
			String contentLength = "-1";
			if (lenHeader != null) {
				contentLength = lenHeader.getValue();
			}

			long length = entity.getContentLength();
			downLoadcontentLength = length;
			System.out.println("downLoadContentLength===="+downLoadcontentLength);

			if (length == -1) {
				length = Long.valueOf(contentLength);
			}

			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			String fileName = "";
			try {
				if (params[1] == null || "".equals(params[1])) {
					if (contentDisposition == null
							|| "".equals(contentDisposition)) {
						Exception e = new Exception();
						exceptionMessage.getData().putSerializable(
								"excepttion", e);
						handler.sendMessage(exceptionMessage);
					} else {
						fileName = contentDisposition;
					}
				} else {
					fileName = params[1];
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				exceptionMessage.getData().putSerializable("excepttion", ex);
				handler.sendMessage(exceptionMessage);
				return null;
			}
			String writeType = PRIVATE;
			try {
				writeType = params[2];
			} catch (Exception ex) {
				ex.printStackTrace();
				exceptionMessage.getData().putSerializable("excepttion", ex);
				handler.sendMessage(exceptionMessage);
				return null;
			}
			String moduleName = "";
			try {
				moduleName = params[3];
			} catch (Exception ex) {
				exceptionMessage.getData().putSerializable("excepttion", ex);
				handler.sendMessage(exceptionMessage);
				ex.printStackTrace();
				return null;
			}

			Log.d("cube", "url=" + params[0] + " fileName=" + fileName
					+ " writeType=" + writeType + " moduleName=" + moduleName);
			if (is != null) {
				if (PRIVATE.equals(writeType)) {
					if ("".equals(moduleName)) {
						fileOutputStream = context.openFileOutput(fileName,
								Context.MODE_WORLD_READABLE);
					} else {
						File moduleDir = context.getDir(moduleName,
								Context.MODE_PRIVATE);
						fileOutputStream = new FileOutputStream(new File(
								moduleDir, fileName));
					}
				} else {
					if ("".equals(moduleName)) {
						fileOutputStream = new FileOutputStream(new File(
								Environment.getExternalStorageDirectory(),
								fileName));
					} else {
						FileWriterUtil.mkdirInSdcard(moduleName);
						String dirpath = Environment
								.getExternalStorageDirectory()
								+ "/"
								+ moduleName;
						fileOutputStream = new FileOutputStream(new File(
								dirpath, fileName));
					}

				}

				byte[] buf = new byte[1024 * 256];
				int ch = -1;
				int count = 0;
				Log.d("download file", "contentLength is " + contentLength);
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					count += ch;
					publishProgress(length == -1 ? -1
							: (int) ((count * 100) / length));
				}
				Log.d("download file", "down load count "+ count);
				publishProgress(100);
				System.out.println("count =="+count);
				if(downLoadcontentLength != count) {
					handler.sendEmptyMessage(0);//�ļ�������ɺ�У���Ƿ�����ļ��������
				}else {
				}
			}
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
			

			if (is != null) {
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
//			exceptionMessage.getData().putSerializable("excepttion", e);
//			handler.sendMessage(exceptionMessage);
			handler.sendEmptyMessage(0);
		}
		return null;
	}

	protected void doHttpFail(Exception e) {
//		ThreadPlatformUtils.finishDownloadTask(this);
	}
	
	public void run(String url,Setting setting){
		if(setting ==null){
			ThreadPlatformUtils.executeByPalform(this, 
					new String[] { url,"com.cube.test.zip",DownloadFileAsyncTask.SDCARD, context.getPackageName() });
		}else{
			ThreadPlatformUtils.executeByPalform(this, 
					new String[] { url,setting.getFilename(),DownloadFileAsyncTask.SDCARD, setting.getTarget() });
		}
	}
	
	public static class Setting{
		String filename;
		String target;
		public Setting(String filename,String target) {
			this.filename = filename;
			this.target = target;
		}
		
		public String getFilename() {
			return filename;
		}
		
		public String getTarget() {
			return target;
		}
	}
}

