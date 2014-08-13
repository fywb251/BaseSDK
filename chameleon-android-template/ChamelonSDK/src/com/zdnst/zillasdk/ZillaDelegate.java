package com.zdnst.zillasdk;

public interface ZillaDelegate {
	public void requestStart();
	public void requestSuccess(String result);
	public void requestFailed(String errorMessage);
}
