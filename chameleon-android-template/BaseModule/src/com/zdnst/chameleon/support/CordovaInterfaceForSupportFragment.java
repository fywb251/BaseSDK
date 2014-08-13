package com.zdnst.chameleon.support;

import org.apache.cordova.CordovaInterface;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public interface CordovaInterfaceForSupportFragment extends CordovaInterface{
	
	public abstract FragmentActivity getActivity();
	
	public abstract Fragment getFragment();
	
}
