
package com.zdnst.chameleon.httputil;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;



public abstract class HttpRequestAsynTask extends GeneralAsynTask {

	private static final int EXCEPTION_MESSAGE = 0;
	/**
	 * @param context
	 */
	protected Context context;
	Exception exception = null;

	public HttpRequestAsynTask(Context context) {
		super(context);
		this.context = context;
	}
	
	public HttpRequestAsynTask(Context context, ProgressDialog progressDialog) {
		super(context, progressDialog);
		this.context = context;
	}
	
	// 此方法在UI线程中执行
	// 当后台计算结束时，调用 UI线程。后台计算结果作为一个参数传递到这步
	@Override
	protected void onPostExecute(String result) { // 操作UI
		super.onPostExecute(result);
		ThreadPlatformUtils.finishTask(this);
		if (exception!=null ){
			doHttpFail(exception);
			return;
		}
		if (stopped) {
			return;
		}
		
		if ((result == null || "".equals(result)) ) {
			if(needProgressDialog){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示");
				builder.setMessage("连接失败，请检查网络");
				builder.setPositiveButton("确定",null);
				Dialog dialog = builder.create();
				dialog.show();
			}
			return;
		}
		else if(result.equals("400")){
			if(needProgressDialog){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示");
				builder.setMessage("应用资源错误");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
//						Application.class.cast(context.getApplicationContext()).logOff();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
			return;
		}else if(result.equals("404")){
			if(needProgressDialog){
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("提示");
				builder.setMessage("远程服务器出错");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					//	Application.class.cast(context.getApplicationContext()).logOff();
					}
				});
				Dialog dialog = builder.create();
				dialog.show();
			}
			return;
		}
		if (!stopped) {
			Log.i("AAAAA HttpRequest","result = "+result);
			doPostExecute(result);
		}
	}
	
	@Override
	public void doPreExecuteBeforeDialogShow() {
		super.doPreExecuteBeforeDialogShow();
		ThreadPlatformUtils.addTask2List(this);
		this.exception = null;
	}

	@Override
	public void doPreExecuteWithoutDialog() {
		super.doPreExecuteWithoutDialog();
		ThreadPlatformUtils.addTask2List(this);
		this.exception = null;
	}

	@Override
	protected void doPostExecute(String result) {
		
	}
	
	public void setDailogMessage(String message){
		
	}
	@Override
	protected String doInBackground(String... params) { 
		String result = "";
		try{
			result = HttpUtil.doWrapedHttp(context, params);
			this.exception = null;
		}catch(Exception e){
			
//			Message msg =  new Message();
//			msg.what=EXCEPTION_MESSAGE;
//			Bundle bundle=new Bundle();
//			bundle.putSerializable("exception", e);
//			msg.setData(bundle);
//			
//			handler.sendMessage(msg);
//			doHttpFail(e);
			this.exception = e;  
		}
		return result;
	}

	protected void doHttpFail(Exception e) {
		
	}
	
//	task.execute(url, "", HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_GET);

	public void run(String url){
		this.execute(url,"",HttpUtil.UTF8_ENCODING,HttpUtil.HTTP_GET);
	}
	
	public void run(String url,Setting setting){
		this.execute(url,setting.getParams(),setting.getEncoding(),setting.getMethod());
	}

	
	
	public static class Setting{
		
		String method;
		String encoding;
		String timeout;
		String params;
		
		
		public Setting(String method,String params,String encoding,String timeout) {
			
			this.method=method;
			this.encoding=encoding;
			this.timeout=timeout;
			this.params = params;
		}
		public String getEncoding() {
			return encoding;
		}
		public String getParams() {
			return params;
		}
		public String getTimeout() {
			return timeout;
		}
		public String getMethod() {
			return method;
		}
	}
}
