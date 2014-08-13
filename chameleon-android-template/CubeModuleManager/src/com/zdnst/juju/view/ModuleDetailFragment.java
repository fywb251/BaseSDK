package com.zdnst.juju.view;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.chameleon.manager.R;
import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.bsl.util.PropertiesUtil;
import com.zdnst.bsl.util.imageTool.CubeAsyncImage;
import com.zdnst.juju.CmanagerModuleActivity;
import com.zdnst.juju.manager.CubeModuleManager;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.juju.model.CubeModule;
import com.zdnst.juju.model.UserPrivilege;
import com.zdnst.push.tool.PadUtils;
import com.zdnst.zdnstsdk.config.CubeConstants;
import com.zdnst.zdnstsdk.config.URL;
import com.zdnst.zillasdk.Zilla;
import com.zdnst.zillasdk.ZillaDelegate;

public class ModuleDetailFragment extends Fragment {
	// titlebar
	private LinearLayout titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;
	private ItemButton app_dealbtn;
	private ImageView icon;
	private ImageView appupData;
	private TextView name;
	private TextView version;
	private TextView releaseNote;
	private CubeModule cubeModule;
	private ProgressBar bar;
	public ProgressDialog progressDialog;
	// 图片滑动
	private SlidePageView pageView;
	private LinearLayout pointlayout;
	private List<SoftReference<Bitmap>> list = new ArrayList<SoftReference<Bitmap>>();

	private String iden;
	
	private Activity activity;
	
	IntentFilter filter =  new IntentFilter();
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(BroadcastConstans.UpdateProgress.equals(intent.getAction())){
				
				if ((CubeModule.INSTALLING == cubeModule.getModuleType()
						|| CubeModule.UPGRADING == cubeModule.getModuleType() 
						|| CubeModule.DELETING == cubeModule.getModuleType()) && cubeModule.getProgress() == -1) {
					bar.setVisibility(View.VISIBLE);
					bar.setIndeterminate(true);
					app_dealbtn.initModel(activity, cubeModule);
				} else if (CubeModule.INSTALLING == cubeModule.getModuleType()
						|| CubeModule.UPGRADING == cubeModule.getModuleType()
						|| CubeModule.DELETING == cubeModule.getModuleType()) {
					bar.setVisibility(View.VISIBLE);
					bar.setIndeterminate(false);
					icon.setAlpha(90);
					bar.setProgress(cubeModule.getProgress());
					app_dealbtn.initModel(activity, cubeModule);
				} else {
					bar.setVisibility(View.GONE);
					icon.setAlpha(255);
					app_dealbtn.initModel(activity, cubeModule);
				}
			}  else if (BroadcastConstans.SecurityRefreshModuelDetail.equals(intent
					.getAction())) {
				ArrayList<String> getList = UserPrivilege.getInstance().getGetList();
				
				ArrayList<String> deleteList = UserPrivilege.getInstance().getDeleteList();
				String identifier = cubeModule.getIdentifier();
				if (!deleteList.contains(identifier) && !getList.contains(identifier)){
					getActivity().finish();
					Toast.makeText(context, "你没有模块使用权限", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (deleteList.contains(identifier)){
					app_dealbtn.setVisibility(View.VISIBLE);
					Toast.makeText(context, "你拥有模块删除权限", Toast.LENGTH_SHORT).show();
				} else {
					app_dealbtn.setVisibility(View.GONE);
					Toast.makeText(context, "你没有模块删除权限", Toast.LENGTH_SHORT).show();
				}
				
			}    else if (BroadcastConstans.SecurityRoleChange.equals(intent
					.getAction())) {
				Toast.makeText(context, "你没有模块使用权限", Toast.LENGTH_SHORT).show();
				getActivity().finish();
			}  
		}
	};
;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		getTheCubeModule();
		if(cubeModule!=null) {
			iden =  cubeModule.getIdentifier()+"_"+cubeModule.getVersion()+"_"+cubeModule.getBuild();
		}
		filter.addAction(BroadcastConstans.UpdateProgress);
		filter.addAction(BroadcastConstans.SecurityRefreshModuelDetail);
		filter.addAction(BroadcastConstans.SecurityRoleChange);
		activity.registerReceiver(broadcastReceiver, filter);
		return inflater.inflate(R.layout.app_detail, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		progressDialog = new ProgressDialog(activity);
		titlebar_left = (LinearLayout) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnTouchListener(onTouchListener);
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("模块详情");
		icon = (ImageView) view.findViewById(R.id.icon);
		bar = (ProgressBar) view.findViewById(R.id.progressBar_download);
		app_dealbtn = (ItemButton) view.findViewById(R.id.app_dealbtn);
		
		if ((CubeModule.INSTALLING == cubeModule.getModuleType() || CubeModule.UPGRADING == cubeModule
				.getModuleType()) && cubeModule.getProgress() == -1) {
			bar.setVisibility(View.VISIBLE);
			bar.setIndeterminate(true);
			app_dealbtn.initModel(activity, cubeModule);
		} else if (CubeModule.INSTALLING == cubeModule.getModuleType()
				|| CubeModule.UPGRADING == cubeModule.getModuleType()) {
			Log.d("Status", cubeModule.getName() + "module.progress-->"
					+ cubeModule.getProgress());
			bar.setVisibility(View.VISIBLE);
			bar.setIndeterminate(false);
			icon.setAlpha(90);
			bar.setProgress(cubeModule.getProgress());
			app_dealbtn.initModel(activity, cubeModule);
		} else {
			bar.setVisibility(View.GONE);
			icon.setAlpha(255);
			app_dealbtn.initModel(activity, cubeModule);
		}
		appupData = (ImageView) view.findViewById(R.id.imageView_updata);
		if (CubeModule.UPGRADABLE == cubeModule.getModuleType()
				|| CubeModule.UPGRADING == cubeModule.getModuleType()) {
			appupData.setVisibility(View.VISIBLE);
		} else {
			appupData.setVisibility(View.GONE);
		}

		// app_dealbtn.setOnTouchListener(onTouchListener);

		app_dealbtn.initModel(activity, cubeModule);
		name = (TextView) view.findViewById(R.id.name);
		version = (TextView) view.findViewById(R.id.version);
		releaseNote = (TextView) view.findViewById(R.id.releaseNote);
		if(cubeModule.getLocal()!=null) {
			
			PropertiesUtil propertiesUtil = PropertiesUtil
					.readProperties(activity, CubeConstants.CUBE_CONFIG);
			// 判断本地模块是否存在
			String icons = propertiesUtil.getString(
					"icon_"+cubeModule.getIdentifier(), "");
			cubeModule.setInstallIcon(icons);
		}
		if(cubeModule.getInstallIcon()!=null) {
			setImageIcon(cubeModule.getInstallIcon(), icon, (View) icon.getParent());
		}else {
			setImageIcon(cubeModule.getIcon(), icon, (View) icon.getParent());
		}
		name.setText(cubeModule.getName());
		version.setText(cubeModule.getVersion());
		releaseNote.setText(cubeModule.getReleaseNote());

		pageView = (SlidePageView) view.findViewById(R.id.slidePageView);
		pointlayout = (LinearLayout) view.findViewById(R.id.point);
		try {
			drawSnapshot(cubeModule, pageView, pointlayout);
//			pageView.setOnPageChangedListener(changedListener);
		} catch (Exception e) {
			e.printStackTrace();
			// Toast.makeText(activity, "加载快照异常",
			// Toast.LENGTH_SHORT).show();
		}

	}

	public void getTheCubeModule() {
		String fromType = null;
		String indentifier = null;
		//直接传入fragment
		if(getArguments() != null){
			fromType = getArguments().getString("FROM_UPGRAGE");
			indentifier = getArguments().getString("identifier");
		}else{
			//通过activity传入
			fromType = activity.getIntent().getStringExtra("FROM_UPGRAGE");
			indentifier = activity.getIntent().getStringExtra("identifier");
		}
		
		if (fromType == null) {
			// 从已安装列表跳入或未安装列表跳入
			if (null != CubeModuleManager.getInstance().getIdentifier_old_version_map()
					.get(indentifier)) {
				cubeModule = CubeModuleManager.getInstance().getIdentifier_old_version_map()
						.get(indentifier);
			} else {
				cubeModule = CubeModuleManager.getInstance()
						.getCubeModuleByIdentifier(indentifier);
			}
		} else {
			// 从可更新列表获取
			cubeModule = CubeModuleManager.getInstance().getIdentifier_new_version_map()
					.get(indentifier);
		}
		Log.i("", "cubeModule ============ "+cubeModule);

		// Log.d("AppDetail",cubeModule.getName()+"--->"+cubeModule.getBuild()+"-->"+cubeModule.getModuleType());
	}

	public void setImageIcon(String url, ImageView icon, final View parent) {
		CubeAsyncImage asyncImageLoader = new CubeAsyncImage(activity);
		
		if (url == null) {
			// 没有从服务器中下载到头像，则至为默认头像
			icon.setImageResource(R.drawable.defauit);
		} else if (url.startsWith("file:")) {
			// String urlName = url.substring(6);
			String urlName = url.substring(url.indexOf("www"));
			AssetManager asm = activity.getAssets();
			java.io.InputStream inputStream = null;
			try {
				// String url2 =
				// "file:///android_asset/www/res/icon/android/icon-chat.png";
				// inputStream = asm.open("www/res/icon/android/" + urlName);
				inputStream = asm.open(urlName);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				Bitmap myBitmap = BitmapFactory.decodeStream(inputStream, null, options);
				icon.setImageBitmap(myBitmap);
				list.add(new SoftReference<Bitmap>(myBitmap));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// icon.setImageDrawable(myBitmap);
			icon.setTag(url.toString());

		} else if (url.startsWith("snapshot:")) {
			// String urlName=url.substring(6);
			int index = url.indexOf(":");
			url = url.substring(index + 1);
			AssetManager asm = activity.getAssets();
			java.io.InputStream inputStream = null;
			try {
				inputStream = asm.open("image/snapshot/" + url);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap myBitmap = BitmapFactory.decodeStream(inputStream, null, options);
				icon.setImageBitmap(myBitmap);
				list.add(new SoftReference<Bitmap>(myBitmap));
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (inputStream != null)
						inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// icon.setImageDrawable(drawable);
		} else {
			
			icon.setTag(url.toString());
			// 异步加载图片，从缓存，内存卡获取图片。
			Bitmap bitmap = asyncImageLoader.loadImage(url.toString(),
					new CubeAsyncImage.ImageCallback() {// 如果无法从缓存，内存卡获取图片，则会调用该方法从网络上下载
						public void imageLoaded(Bitmap bitmap, String imageUrl) {
							ImageView imageViewByTag = (ImageView) parent
									.findViewWithTag(imageUrl);
							if (imageViewByTag == null) {
								return;
							}
							if (null != bitmap) {
								imageViewByTag.setImageBitmap(bitmap);
							} else {
								imageViewByTag
										.setImageResource(R.drawable.defauit);
							}
						}
					});

			if (bitmap != null) {
				// 设置图片显示
				list.add(new SoftReference<Bitmap>(bitmap));
				icon.setImageBitmap(bitmap);
			} else {
				icon.setImageResource(R.drawable.defauit);
			}
		}
	}

//	OnPageChangedListener changedListener = new com.zdnst.chameleon.phone.view.SlidePageView.OnPageChangedListener() {
//
//		@Override
//		public void onPageViewChanged(View view, int currentScreen) {
//			for (int i = 0; i < pointlayout.getChildCount(); i++) {
//				if (i == currentScreen) {
//					pointlayout.getChildAt(i).setBackgroundResource(
//							R.drawable.app_detail_selected);
//				} else {
//					pointlayout.getChildAt(i).setBackgroundResource(
//							R.drawable.app_detail_unselected);
//				}
//			}
//		}
//	};

	OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(v.getId() == R.id.title_barleft ) {
				if(activity != null && PadUtils.isPad(activity)){
					if(activity instanceof CmanagerModuleActivity){
						((CmanagerModuleActivity)activity).showDetailContent(false);
					}
				}else{
					if(activity!=null) {
//						AppDetailActivity activity = (AppDetailActivity) activity;
						activity.finish();
					}
				}
			}
			return false;
		}
	};

	/**
	 * 根据identifier和build获取模块的快照图
	 * 
	 * @param identifier
	 *            ,build
	 */
	private void drawSnapshot(CubeModule module, final SlidePageView pageView,
			final LinearLayout pointlayout) {
		String appKey = URL.APPKEY;
		String identifier = module.getIdentifier();
		String version = module.getVersion();
		
		View view = LayoutInflater.from(activity).inflate(R.layout.app_detail_item, null);
		final ImageView img = (ImageView) view.findViewById(R.id.detail_img);
		final LinearLayout loadinglayout = (LinearLayout) view.findViewById(R.id.detail_loadingImglayout);
		final TextView loadingtext = (TextView) view.findViewById(R.id.detail_loadtext);
		final ProgressBar loadingbar = (ProgressBar) view.findViewById(R.id.detail_loadingBar);
		pageView.addView(view);
		
		ZillaDelegate delegate = new ZillaDelegate() {
			
			@Override
			public void requestStart() {
				img.setVisibility(View.GONE);
				loadinglayout.setVisibility(View.VISIBLE);
				
			}

			@Override
			public void requestSuccess(final String result) {
				if (result != null) {
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							try {
								JSONArray jsonary = new JSONArray(result);
								int length = jsonary.length();
								List<String> paths = new ArrayList<String>();
								if (length != 0) {
									if (pageView.getChildCount() != 0) {
										pageView.removeAllViews();
									}
									for (int i = 0; i < jsonary.length(); i++) {
										paths.add(URL.getDownloadUrl(activity,jsonary.getJSONObject(i).getString("snapshot")));
									}
									draw(paths, pageView, pointlayout);
								} else {
									img.setVisibility(View.GONE);
									loadinglayout.setVisibility(View.VISIBLE);
									loadingbar.setVisibility(View.GONE);
									loadingtext.setText("没有快照");

								}
							} catch (Exception e) {
								img.setVisibility(View.GONE);
								loadinglayout.setVisibility(View.VISIBLE);
								loadingbar.setVisibility(View.GONE);
								loadingtext.setText("图片加载出错");
								e.printStackTrace();
							}
						}
					});
				}
				
			}
			
			
			@Override
			public void requestFailed(String errorMessage) {
				activity.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						img.setVisibility(View.GONE);
						loadinglayout.setVisibility(View.VISIBLE);
						loadingbar.setVisibility(View.GONE);
						loadingtext.setText("网络状态不稳定!");
					}
					
				});
			}
		};


		if (module.getLocal() != null) {
			String[] files = CubeApplication.getInstance(activity).tool
					.getAssectFilePath(module.getIdentifier());

			if (files.length != 0) {
				if (pageView.getChildCount() != 0) {
					pageView.removeAllViews();
				}
				List<String> paths = new ArrayList<String>();
				for (int i = 0; i < files.length; i++) {
					paths.add("snapshot:" + identifier + "/" + files[i]);
				}
				draw(paths, pageView, pointlayout);
			} else {
				img.setVisibility(View.GONE);
				loadinglayout.setVisibility(View.VISIBLE);
				loadingbar.setVisibility(View.GONE);
				loadingtext.setText("没有快照");
			}

		} else {
			Zilla.getZilla().snapshot(activity,delegate,appKey,identifier,version);
		}
	}

	private void draw(List<String> files, SlidePageView pageView,
			final LinearLayout pointlayout) {
		if (files.size() != 0) {
			if (pageView.getChildCount() != 0) {
				pageView.removeAllViews();
			}
			for (int i = 0; i < files.size(); i++) {
				if(activity==null) {
					return;
				}

				View view = LayoutInflater.from(activity).inflate(
						R.layout.app_detail_item, null);
				LayoutParams layoutParams = new LayoutParams(400,
						LayoutParams.MATCH_PARENT);
				layoutParams.setMargins(0, 0, 10, 0);
				view.setLayoutParams(layoutParams);
				view.setPadding(20, 10, 20, 0);
				ImageView img2 = (ImageView) view.findViewById(R.id.detail_img);
				setImageIcon(files.get(i), img2, view);
				pageView.addView(view, i);
				View viewPoint = new View(activity);
				LayoutParams pointParams = new LayoutParams(files.size(),
						files.size());
				pointParams.setMargins(10, 0, 10, 0);
				viewPoint.setLayoutParams(pointParams);
				if (i == 0) {
					viewPoint
							.setBackgroundResource(R.drawable.app_detail_selected);
				} else {
					viewPoint
							.setBackgroundResource(R.drawable.app_detail_selected);
				}
				pointlayout.addView(viewPoint, i);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroy();
//		EventBus.getEventBus(TmpConstants.EVENTBUS_MODULE_CHANGED,
//				ThreadEnforcer.MAIN).unregister(this);
		activity.unregisterReceiver(broadcastReceiver);
		List<SoftReference<Bitmap>> copies = new ArrayList<SoftReference<Bitmap>>();
		for (SoftReference<Bitmap> srf : list) {
			if (srf != null && srf.get() != null && !srf.get().isRecycled()) {
				srf.get().recycle();
				copies.add(srf);
			}
		}
		list.removeAll(copies);
	}

	public String getIdentifier() {
		return cubeModule.getIdentifier();
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-9-5 下午11:35:44
	 */
	@Override
	public String toString() {
		return iden==null?this.getClass().getName()+this.hashCode():iden;
	}
}
