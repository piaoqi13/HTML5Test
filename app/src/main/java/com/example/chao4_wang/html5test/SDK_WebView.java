package com.example.chao4_wang.html5test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import io.dcloud.EntryProxy;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.ISysEventListener.SysEventType;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;

/**
 * 本demo为以webview控件方式集成5+ sdk， 
 *
 */
public class SDK_WebView extends Activity {

	boolean doHardAcc = true;
	EntryProxy mEntryProxy = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (mEntryProxy == null) {
			FrameLayout rootView = new FrameLayout(this);
			WebviewModeListener wm = new WebviewModeListener(this, rootView);
			mEntryProxy = EntryProxy.init(this, wm);
			mEntryProxy.onCreate(this, savedInstanceState, SDK.IntegratedMode.WEBVIEW, null);
			setContentView(rootView);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return mEntryProxy.onActivityExecute(this, SysEventType.onCreateOptionMenu, menu);
	}

	@Override
	public void onPause() {
		super.onPause();
		mEntryProxy.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mEntryProxy.onResume(this);
	}

	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getFlags() != 0x10600000) {
			mEntryProxy.onNewIntent(this, intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEntryProxy.onStop(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyDown, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyUp, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyLongPress, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyLongPress(keyCode, event);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		try {
			int temp = this.getResources().getConfiguration().orientation;
			if (mEntryProxy != null) {
				mEntryProxy.onConfigurationChanged(this, temp);
			}
			super.onConfigurationChanged(newConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mEntryProxy.onActivityExecute(this, SysEventType.onActivityResult, new Object[] { requestCode, resultCode, data });
	}

	// for test
	public void skipAct(String param) {
		Log.e("CollinWang", "skipAct have run");
		Log.e("CollinWang", "param=" + param);

		Intent intent = new Intent(SDK_WebView.this, NativeActivity.class);
		SDK_WebView.this.startActivity(intent);
	}

}

class WebviewModeListener implements ICoreStatusListener {

	LinearLayout btns = null;
	Activity activity = null;
	ViewGroup mRootView = null;
	IWebview webview = null;
	ProgressDialog pd = null;

	public WebviewModeListener(Activity activity, ViewGroup rootView) {
		this.activity = activity;
		mRootView = rootView;
		btns = new LinearLayout(activity);
		mRootView.setBackgroundColor(0xffffffff);
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				webview.onRootViewGlobalLayout(mRootView);
			}
		});
	}

	@Override
	public void onCoreInitEnd(ICore coreHandler) {
		String appid = "com.example.chao4_wang.html5test";
		String url = "file:///android_asset/apps/com.example.chao4_wang.html5test/www/index.html";
		webview = SDK.createWebview(activity, url, appid, new IWebviewStateListener() {
			@Override
			public Object onCallBack(int pType, Object pArgs) {
				switch (pType) {
				case IWebviewStateListener.ON_WEBVIEW_READY:
					((IWebview) pArgs).obtainFrameView().obtainMainView().setVisibility(View.INVISIBLE);
					SDK.attach(mRootView, ((IWebview) pArgs));
					break;
				case IWebviewStateListener.ON_PAGE_STARTED:
					break;
				case IWebviewStateListener.ON_PROGRESS_CHANGED:
					break;
				case IWebviewStateListener.ON_PAGE_FINISHED:
					webview.obtainFrameView().obtainMainView().setVisibility(View.VISIBLE);
					break;
				}
				return null;
			}
		});

		final WebView webviewInstance = webview.obtainWebview();
		webviewInstance.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (webviewInstance.canGoBack()) {
						webviewInstance.goBack();
						return true;
					}
				}
				return false;
			}
		});
	}

	@Override
	public void onCoreReady(ICore coreHandler) {
		try {
			SDK.initSDK(coreHandler);
			SDK.requestAllFeature();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCoreStop() {
		return false;
	}
}
