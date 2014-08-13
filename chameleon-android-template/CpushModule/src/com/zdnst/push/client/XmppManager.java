package com.zdnst.push.client;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.zdnst.bsl.util.Preferences;
import com.zdnst.juju.CheckInUtil;
import com.zdnst.push.tool.NetworkUtil;
import com.zdnst.push.tool.PropertiesUtil;



public class XmppManager {

	/**
	 * [客户端资源名称]
	 */
	private static final String XMPP_RESOURCE_NAME = "Cube_Client";

	/**
	 * [配置储存]
	 */
	private SharedPreferences sharedPrefs;

	/**
	 * [唯一连接]
	 */
	public XMPPConnection connection;

	/**
	 * [主机地址]
	 */
	private String xmppHost;

	/**
	 * [主机端口]
	 */
	private int xmppPort;

	/**
	 * [用户名]
	 */
	private String usernameStore="";

	/**
	 * [密码]
	 */
	private String passwordStore="";

	/**
	 * [连接状态监听]
	 */
	private ConnectionListener connectionListener;

	/**
	 * [通知监听]
	 */
	private static PacketListener notificationPacketListener;

	/**
	 * [Presence状态监听]
	 */

//	/**
//	 * [监听Roster CRUD]
//	 */
//	private RosterListener rosterListener;

	/**
	 * [重连线程]
	 */
	private ReconnectionThread reconnection;
	
	/**
	 * 通知服务
	 */
	private NotificationService notificationService;

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	/**
	 * 线程池
	 */
	private ExecutorService pool;

	public XmppManager(NotificationService notificationService, int resourceId) {
		this.notificationService = notificationService;
		this.sharedPrefs = notificationService.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(notificationService, resourceId);
//		String token = PushUtil.createMD5Token(getNotificationService().getApplicationContext());
		
//		this.usernameStore = username ;
//		this.passwordStore = token ;
		notificationPacketListener = new NotificationPacketListener();
		this.xmppHost = propertiesUtil.getString("xmppHost", "127.0.0.1");
		this.xmppPort = propertiesUtil.getInteger("xmppPort", 5222);
		connectionListener = new PersistentConnectionListener(this);
		reconnection = new ReconnectionThread(this);
		pool = Executors.newCachedThreadPool();
		addProviders();
	}

	public void addProviders() {
		ProviderManager pm = ProviderManager.getInstance();
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());
		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient",
					"Can't load class for org.jivesoftware.smackx.packet.Time");
		}
		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster",
				new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());
		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());
		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user",
				new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin",
				new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner",
				new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());
		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());
		// Privacy
		pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());

	}


	class Entry {

		public Entry(String name, String password) {
			super();
			this.name = name;
			this.password = password;
		}

		private String name;
		private String password;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}

	/**
	 * [请求队列]
	 */
	private BlockingQueue<Entry> reqConnectQueue = new ArrayBlockingQueue<Entry>(
			10);

	/**
	 * [准备连接]<BR>
	 * 1.每个连接请求排队，应对并发<BR>
	 */
	public void prepairConnect() {
		Log.d("xmppManager","xmpp manager prepairConnect...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Entry entry = reqConnectQueue.take();
						Log.d("xmppManager",
								"take a login entry req for username:{} password:{} and connecting..."+
								entry.getName()+ " " + entry.getPassword());
						if (notificationService != null
								&& !NetworkUtil
										.isNetworkConnected(notificationService)) {
//							EventBus.getEventBus(TmpConstants.COMMNAD_INTENT)
//							.post(ConnectStatusChangeEvent.SHOW_TOAST);
							//to
							continue;
						}
						if(entry!=null)
							
							if(!connect(entry.getName(), entry.getPassword()))
								{
									//第一次连接失败，重连
									reconnect();
								}
					} catch (InterruptedException e) {
						Log.e("xmppManager","connect xmpp server error!"+ e);
					}
				}
			}
		}).start();
	}

	public void submitConnectReq(String username, String password) {
		Log.d("xmppManager","submitConnectReq for username:{},password:{}"+username+" "+
				password);
		try {
			reqConnectQueue.add(new Entry(username, password));
			
		} catch (Exception e) {
			reqConnectQueue.clear();
			reqConnectQueue.add(new Entry(username, password));
		}
	}

	public void submitReconnectReq() {
		Log.d("xmppManager","submitReconnectReq for username:{},password:{}"+
				usernameStore+" "+passwordStore);
		try {
			reqConnectQueue.add(new Entry(usernameStore, passwordStore));
			
		} catch (Exception e) {
			reqConnectQueue.clear();
			reqConnectQueue.add(new Entry(usernameStore, passwordStore));
		}
	}

	private boolean connect(String username, String password) {
		boolean result = false;
			if (connection != null) {
				boolean isAuth = connection.isAuthenticated();
				boolean isUserStore = usernameStore.equals(username);
				if (isUserStore && isAuth) {
					Log.d("the same user {} has logined,ignored conect...",
							username);
					reqConnectQueue.clear();
					return true;
				} else {
					Log.d("xmppManager","xmpp connection has connect,but not authenticated ,close it!");
					connection.disconnect();
					connection = null;
				}
			}
			if (usernameStore == null) {
				usernameStore = username;
				passwordStore = password;
			}
		try {	
			
			XMPPConnection xmppConnection = pool.submit(new ConnectTask(null)).get();
			Log.d("xmppManager","authenticated the xmpp connection created by previous!");
			this.connection= xmppConnection;
			//这里 submit LoginTask !! tmd！草！
			result = connect(xmppConnection, username, password);
		} catch (InterruptedException e) {
			Log.e("xmppManager","connect() InterruptedException", e);
			result = false;
		} catch (ExecutionException e) {
			Log.e("xmppManager","connect() ExecutionException", e);
			result = false;
		}
//		if (result) {
//			// 设置当前的usermodel
//			meJid = username + "@" + this.getConnection().getServiceName();
//			sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_CHAT,
//					ConnectStatusChangeEvent.CONN_STATUS_ONLINE);
//		} else {
//			sendBroadcastWithStatus(ConnectStatusChangeEvent.CONN_CHANNEL_CHAT,
//					ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
//		}
		return result;
	}

	private boolean connect(XMPPConnection xmppConnection, String username,
			String password) {
		Log.d(" {} is authenticating...", username);
		try {
			String errorMsg = pool.submit(new LoginTask(xmppConnection, this, username, password)).get();
			Log.d("xmppManager"," {}'s athentication result is {}"+ username+" "+ errorMsg);
			String INVALID_CREDENTIALS_ERROR_CODE = "401";
			if (errorMsg != null && "".equals(errorMsg)) {
				
				Log.d("authentication is success for username:{},retrun..", username);
				
				reqConnectQueue.clear();
				return true;
			} else if (errorMsg != null && errorMsg.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
				Log.i("xmppManager","uesrname {} has not been registered,begin register ...."+username);
				boolean registerSuccess = pool.submit(new RegisterTask(xmppConnection, username, password)).get();

				if (registerSuccess) {
					Log.i("xmppManager","username:{} register successfully,retrying connect..." + 
							username);
					connect(username, password);
				} else {
					Log.e("xmppManager","username:{} register faill...."+ username);
				}
			}
		} catch (Exception e) {
			Log.e("xmppManager","connect xmpp server execption...."+ e);

		}
		if (xmppConnection != null && xmppConnection.isConnected()) {
			Log.d("xmppManager","disconnect connection...");
			xmppConnection.disconnect();
		}

		return false;
	}

	public void reconnect() {
		if (usernameStore == null) {
			Log.i("xmppManager","reconnect no username....,return");
		}
		Log.i("xmppManager","submit a reconnect req!");
		submitConnectReq(usernameStore, passwordStore);
	}

	public boolean disconnect() {
		Log.d("xmppManager","disconnect()...");
		try {
			pool.submit(new LogoutTask()).get();
			Log.d("xmppManager","disconnect success!");
			return true;
		} catch (Exception e) {
			Log.e("xmppManager","disconnect error!", e);
			return false;
		}
	}

	public void setConnection(XMPPConnection connection) {
		this.connection = connection;
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	public void registerLogin(String username, String password) {
		Editor editor = sharedPrefs.edit();
		editor.putString(Constants.XMPP_USERNAME, username);
		editor.putString(Constants.XMPP_PASSWORD, password);
		editor.commit();
	}

	public void unregisterLogin() {
		Editor editor = sharedPrefs.edit();
		editor.remove(Constants.XMPP_USERNAME);
		editor.remove(Constants.XMPP_PASSWORD);
		editor.commit();
	}

	public void sendPacket(Packet packet) {
		if (connection != null) {
			connection.sendPacket(packet);
		}
	}

//	public RosterListener getRosterListener() {
//		return rosterListener;
//	}
//
//	public void setRosterListener(RosterListener rosterListener) {
//		this.rosterListener = rosterListener;
//	}

	public String getUsernameStore() {
		return usernameStore;
	}

	public void setUsernameStore(String username) {
		this.usernameStore = username;
	}

	public String getPasswordStore() {
		return passwordStore;
	}

	public void setPasswordStore(String password) {
		this.passwordStore = password;
	}

	public ConnectionListener getConnectionListener() {
		return connectionListener;
	}

	public PacketListener getNotificationPacketListener() {
		return notificationPacketListener;
	}

	public void stopReconnectionThread(){
		Log.i("xmppManager","xmpp manager stopReconnectionThread... ");
		synchronized (reconnection) {
			
			if(reconnection!=null){
				reconnection.stopReconnect();
			}
		}
	}
	
	public void startReconnectionThread() {
		Log.i("xmppManager","xmpp manager startReconnectionThread... ");
		synchronized (reconnection) {
//			if (!reconnection.isAlive()) {
//				reconnection.setName("Xmpp Reconnection Thread");
//				reconnection.start();
//			}
			if (!reconnection.isThreadAlive()) {
				reconnection.setName("Xmpp Reconnection Thread");
				reconnection = new ReconnectionThread(this);
				reconnection.start();
			}
		}
	}

	/******************************************************************************************************
	 * 
	 * lognin.....
	 * 
	 *****************************************************************************************************/

	public boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	public boolean isAuthenticated() {
		return connection != null && connection.isConnected()
				&& connection.isAuthenticated();
	}

	public boolean isRegistered() {
		return sharedPrefs.contains(Constants.XMPP_USERNAME)
				&& sharedPrefs.contains(Constants.XMPP_PASSWORD);
	}

	private class RegisterTask implements Callable<Boolean> {

		private String username;
		private String password;

		private RegisterTask(XMPPConnection xmppConnection, String username,
				String password) {
			this.username = username;
			this.password = password;
		}

		public Boolean call() {
			Log.i("xmppManager",username + " RegisterTask.run()...");
			Registration registration = new Registration();
			registration.setType(IQ.Type.SET);
//			registration.setTo(connection.getServiceName());
			registration.addAttribute("username", username);
			registration.addAttribute("password", password);
			
//			registration.setUsername(username);//注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。  
//			registration.setPassword(password);  
//			registration.addAttribute("android", "geolo_createUser_android");//这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！
			
			PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
					registration.getPacketID()), new PacketTypeFilter(IQ.class));
			PacketCollector packetCollector =null;
			try {
				packetCollector = connection.createPacketCollector(packetFilter);
				connection.sendPacket(registration);
				Packet packet = packetCollector.nextResult();
				Log.d("RegisterTask.PacketListener", "processPacket().....");
				IQ response = (IQ) packet;
				if (response.getType() == IQ.Type.ERROR) {
					if (response.getError().toString().contains("409")) {
						Log.e("xmppManager","Unknown error while registering XMPP account! "
								+ response.getError().getCondition());
						Log.i("xmppManager","register failed");
					}
					return false;
				} else if (response.getType() == IQ.Type.RESULT) {
					Log.i("xmppManager","register success");
					return true;
				}
			} catch (Exception e) {
				Log.i("xmppManager","register failed");
				e.printStackTrace();
				return false;
			}
			

			
			
			return null;
		}
	}

	private class ConnectTask implements Callable<XMPPConnection> {

		private XMPPConnection xmppConnection;

		private ConnectTask(XMPPConnection xmppConnection) {
			this.xmppConnection = xmppConnection;
		}

		public XMPPConnection call() {
			if (xmppConnection == null) {
				ConnectionConfiguration connConfig = new ConnectionConfiguration(
						xmppHost, xmppPort);
				connConfig.setSASLAuthenticationEnabled(false);
				connConfig.setSecurityMode(SecurityMode.disabled);
				connConfig.setRosterLoadedAtLogin(true);
				connConfig.setReconnectionAllowed(false);
				xmppConnection = new XMPPConnection(connConfig);
			}
			Log.i("xmppManager"," ConnectTask.run()...");

			if (!xmppConnection.isConnected()) {
				try {

					xmppConnection.connect();
					Log.i("xmppManager"," XMPP connected successfully");
					return xmppConnection;
				} catch (Exception e) {
					Log.e("xmppManager"," XMPP connection failed", e);
				}
			} else {
				Log.i("xmppManager"," xmppConnection already connected");
			}

			return xmppConnection;
		}
	}

	private class LoginTask implements Callable<String> {

		final XmppManager xmppManager;
		final XMPPConnection xmppConnection;
		private String username;
		private String password;

		public LoginTask(XMPPConnection xmppConnection,
				XmppManager xmppManager, String username, String password) {
			this.xmppConnection = xmppConnection;
			this.xmppManager = xmppManager;
			this.username = username;
			this.password = password;
		}

		public String call() {
			Log.i("xmppManager",username + " LoginTask.run()...");
			if (!xmppConnection.isAuthenticated()) {
				try {
					xmppConnection.login(username, password, XMPP_RESOURCE_NAME);
					Log.v("xmppManager", username + " login success");
					
					
					// 记录成功登陆的连接
					xmppManager.setConnection(xmppConnection);
					// 记住账号密码
					xmppManager.setUsernameStore(username);
					xmppManager.setPasswordStore(password);
					
						//消息推送登录成功
						// 注册监听器
						registerListeners();
						//注册签到
						Presence presence = new Presence(Presence.Type.available, "在线",1, null);
						connection.sendPacket(presence);
						
						Log.d("xmppManager",username + " Loggedn in successfully");
						sendBroadcastWithStatus(
								ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE,
								ConnectStatusChangeEvent.CONN_STATUS_ONLINE);
						
//						Intent intent = new Intent(BroadcastConstans.PushCheckIn);
//						intent.putExtra("serviceName", connection.getServiceName());
//						notificationService.sendBroadcast(intent);
						Preferences.saveServiceName(connection.getServiceName());
						CheckInUtil.registerPush(notificationService.getApplicationContext(), connection.getServiceName());
					return "";

				} catch (XMPPException e) {
					Log.e("xmppManager",username
							+ " Failed to login to xmpp server. Caused by: "
							+ e.getMessage());

					return e.getMessage();

				} catch (Exception e) {
					Log.e("xmppManager",username
							+ " Failed to login to xmpp server. Caused by: "
							+ e.getMessage());
					return e.getMessage();
				}

			} else {
				Log.i("xmppManager",username + " Already athenticated...");
				return "";
			}

		}

		public void registerListeners() {

			PacketFilter messageFilter = new PacketTypeFilter(Message.class);
			PacketListener packetListener = xmppManager.getNotificationPacketListener();
//			RosterListener rosterListener = xmppManager.getRosterListener();
			xmppConnection.addPacketListener(packetListener, messageFilter);
			
//			xmppConnection.getRoster().addRosterListener(rosterListener);
			xmppConnection.addConnectionListener(xmppManager.getConnectionListener());
		}
	}

	public boolean isOnline() {
		return connection != null && connection.isAuthenticated();
	}

	public boolean disconnectNow() {
		boolean result = false;
		if (isConnected()) {
			reqConnectQueue.clear();
			Log.d("xmppManager",usernameStore + " LogoutTask.run()");
			/**
			 * 移除
			 */
			connection.removePacketListener(notificationPacketListener);
//			connection.getRoster().removeRosterListener(rosterListener);
			try {
				connection.disconnect();
				connection = null;
				result = true;
			} catch (Exception e) {
				Log.e("xmppManager","close xmpp connection error!", e);
				result = false;
			}
		} else {
			Log.i("xmppManager",usernameStore + " Already logout...");
			result = true;
		}

		return result;
	}

	private class LogoutTask implements Callable<Boolean> {

		final XmppManager xmppManager = XmppManager.this;

		public Boolean call() {
			boolean result = false;
			if (xmppManager.isConnected()) {
				reqConnectQueue.clear();
				Log.d("xmppManager",usernameStore + " LogoutTask.run()");
				/**
				 * 移除
				 */

				connection.removePacketListener(notificationPacketListener);

//				connection.getRoster().removeRosterListener(rosterListener);
				
				try {
					connection.disconnect();
					connection = null;
					result = true;
					
										
				} catch (Exception e) {
					Log.e("xmppManager","close xmpp connection error!", e);
					result = false;
				}
			} else {
				Log.i("xmppManager",usernameStore + " Already logout...");
				result = true;
			}
			
			if(result){
				
				sendBroadcastWithStatus(
						ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE,
						ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
			}
			
			
			return result;
		}
	}

	private class OfflineTask implements Runnable {

		@Override
		public void run() {
			if (connection != null && connection.isAuthenticated()
					&& connection.isConnected()) {
				Roster roster = connection.getRoster();
				Collection<RosterEntry> entries = roster.getEntries();
				Presence presence = null;
				for (RosterEntry entry : entries) {
					presence = new Presence(Presence.Type.unavailable);
					presence.setFrom(usernameStore + "@"
							+ connection.getServiceName());

					presence.setTo(entry.getUser());
					connection.sendPacket(presence);
				}
				connection.sendPacket(presence);
				sendBroadcastWithStatus(
						ConnectStatusChangeEvent.CONN_CHANNEL_CHAT,
						ConnectStatusChangeEvent.CONN_STATUS_OFFLINE);
			}
		}
	}

	private class OnlineTask implements Runnable {
		public void run() {
			if (connection != null && connection.isAuthenticated()
					&& connection.isConnected()) {
				Presence presence = new Presence(Presence.Type.available, "在线",
						1, null);
				connection.sendPacket(presence);
				sendBroadcastWithStatus(
						ConnectStatusChangeEvent.CONN_CHANNEL_CHAT,
						ConnectStatusChangeEvent.CONN_STATUS_ONLINE);
			}
		}
	}

	public void offline() {
		pool.submit(new OfflineTask());
	}

	public void online() {
		pool.submit(new OnlineTask());
	}

	private void sendBroadcastWithStatus(String channel, String status) {
//		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
//				.post(new ConnectStatusChangeEvent(channel, status));
	}

	public String getXmppServiceName() {

		return connection.getServiceName();
	}

	
}
