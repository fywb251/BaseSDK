package com.zdnst.imsdk.chat;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.ConnectionListener;

import com.zdnst.imsdk.chat.domain.IMMessage;
import com.zdnst.imsdk.chat.listener.LoginListener;

import android.content.Context;


public class ChatManager {

	private static ChatManager chatManager;

	private ThreadPoolExecutor mExecutor;

	private ChatManager() {
	};
	
	public void init(Context mContext)
	{
		
	}

	public ChatManager getInstance() {
		if (null == chatManager) {
			chatManager = new ChatManager();
		}
		return chatManager;
	}

	private void createExecutor() {
		mExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

	}

	private boolean execute(Runnable runnable) {

		if (mExecutor == null)
			createExecutor();
		try {
			mExecutor.execute(runnable);
		} catch (RejectedExecutionException ex) {
			return false;
		}
		return true;
	}

	/**
	 * 登录IM服务器
	 * @param userName
	 * @param passwd
	 * @return
	 */
	public boolean login(String userName, String passwd) {
		return true;
	}

	/**
	 * 带回调方法的登录
	 * @param userName
	 * @param passwd
	 * @param listner
	 * @return
	 */
	public boolean login(String userName, String passwd, LoginListener listner) {
		return true;
	}

	/**
	 * 异步登录
	 * @param userName
	 * @param passwd
	 * @param listner
	 * @return
	 */
	public boolean asyncLogin(String userName, String passwd) {
		return true;
	}
	
	/**
	 * 退出IM服务器
	 * @return
	 */
	public boolean logout()
	{
		return true;
	}
	
	/**
	 * 发送消息
	 * @param msg
	 * @return
	 */
	public boolean sendMessage(IMMessage msg)
	{
		return true;
	}
	
	/**
	 * 发送消息
	 * @param msg
	 * @return
	 */
	public boolean sendMessage(IMMessage msg,IMCallback callback)
	{
		return true;
	}
	
	/**
	 * 从服务端获取该用户的消息
	 * @param userId
	 * @return
	 */
	public List<IMMessage> getMessage(String userId)
	{
		return null;
	}
	
	/**
	 * 标记消息为已经收到
	 * @param msg
	 * @return
	 */
	public boolean markMessageReceived(IMMessage msg)
	{
		return true;
	}
	
	/**
	 * 获取未读消息数
	 * @return
	 */
	public int getUnreadMsgCount()
	{
		return 0;
	}
	
	/**
	 * 是否连接
	 * @return
	 */
	public boolean isConnected()
	{
		return true;
	}
	
	public void addConnectionListener(ConnectionListener connectionListener)
	{
		
	}
	
	
	

}
