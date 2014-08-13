package com.zdnst.juju.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.chameleon.manager.R;
import com.zdnst.chameleon.util.DeviceInfoUtil;
import com.zdnst.zdnstsdk.config.URL;


public class AboutActivity extends Activity {
	private LinearLayout titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;
	private TextView version;
	private TextView devideId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_about);
		initValue();
	}

	private void initValue() {
		titlebar_left = (LinearLayout) findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) findViewById(R.id.title_barcontent);
		titlebar_content.setText("关于我们");

		version = (TextView) findViewById(R.id.about_version);
		devideId = (TextView) findViewById(R.id.about_device);
		
		String versionText = URL.APP_VERSION;
		version.setText(versionText);
		devideId.setText(DeviceInfoUtil.getDeviceId(this));

	}

	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.title_barleft){
				finish();
				
			}
		}
	};
}
