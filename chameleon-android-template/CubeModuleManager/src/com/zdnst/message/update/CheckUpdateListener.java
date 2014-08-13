package com.zdnst.message.update;

import com.zdnst.juju.model.CubeApplication;


public interface CheckUpdateListener {
	void onCheckStart();
	void onUpdateAvaliable(final CubeApplication curApp, final CubeApplication newApp);
	void onUpdateUnavailable();
	void onCheckError(final Throwable error);
	void onCancelled();
}
