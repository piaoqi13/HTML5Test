package com.example.chao4_wang.html5test;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import io.dcloud.EntryProxy;
import io.dcloud.RInformation;
import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.IApp.IAppStatusListener;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.IOnCreateSplashView;
import io.dcloud.common.DHInterface.ISysEventListener.SysEventType;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.common.util.BaseInfo;
import io.dcloud.common.util.ImageLoaderUtil;
import io.dcloud.feature.internal.sdk.SDK;

/**
 * 本demo为以WebApp方式集成5+ sdk，
 */
public class SDK_WebApp extends Activity {

    boolean doHardAcc = true;
    EntryProxy mEntryProxy = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mEntryProxy == null) {
            FrameLayout f = new FrameLayout(this);
            WebappModeListener wm = new WebappModeListener(this, f);
            mEntryProxy = EntryProxy.init(this, wm);
            mEntryProxy.onCreate(this, savedInstanceState, SDK.IntegratedMode.WEBAPP, null);
            setContentView(f);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && BaseInfo.mDeStatusBarBackground == -111111) {
            BaseInfo.mDeStatusBarBackground = getWindow().getStatusBarColor();
        }
        mEntryProxy.onResume(this);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        if (intent.getFlags() != 0x10600000) {
//            mEntryProxy.onNewIntent(this, intent);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEntryProxy.onStop(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyDown, new Object[]{keyCode, event});
        return _ret ? _ret : super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyUp, new Object[]{keyCode, event});
        return _ret ? _ret : super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyLongPress, new Object[]{keyCode, event});
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
        mEntryProxy.onActivityExecute(this, SysEventType.onActivityResult, new Object[]{requestCode, resultCode, data});
    }

    // for test
    public void skipAct(String param) {
        Log.e("CollinWang", "param=" + param);

        Intent intent = new Intent(SDK_WebApp.this, NativeActivity.class);

        SDK_WebApp.this.startActivity(intent);
        this.startActivityForResult(intent, 0);

        Log.e("CollinWang", "skipAct after intent");
    }
}

class WebappModeListener implements ICoreStatusListener, IOnCreateSplashView {
    Activity activity;
    View splashView = null;
    ViewGroup rootView;
    IApp app = null;
    ProgressDialog pd = null;

    public WebappModeListener(Activity activity, ViewGroup rootView) {
        this.activity = activity;
        this.rootView = rootView;
    }

    @Override
    public void onCoreInitEnd(ICore coreHandler) {
        String appBasePath = "/apps/com.example.chao4_wang.html5test";
        String args = "{url:'http://www.baidu.com'}";

        app = SDK.startWebApp(activity, appBasePath, args, new IWebviewStateListener() {
            @Override
            public Object onCallBack(int pType, Object pArgs) {
                switch (pType) {
                    case IWebviewStateListener.ON_WEBVIEW_READY:
                        View view = ((IWebview) pArgs).obtainApp().obtainWebAppRootView().obtainMainView();
                        view.setVisibility(View.INVISIBLE);
                        if (view.getParent() != null) {
                            ((ViewGroup) view.getParent()).removeView(view);
                        }
                        rootView.addView(view, 0);
                        break;
                    case IWebviewStateListener.ON_PAGE_STARTED:
                        pd = ProgressDialog.show(activity, "loading", "0/100");
                        break;
                    case IWebviewStateListener.ON_PROGRESS_CHANGED:
                        if (pd != null) {
                            pd.setMessage(pArgs + "/100");
                        }
                        break;
                    case IWebviewStateListener.ON_PAGE_FINISHED:
                        if (pd != null) {
                            pd.dismiss();
                            pd = null;
                        }
                        app.obtainWebAppRootView().obtainMainView().setVisibility(View.VISIBLE);
                        break;
                }
                return null;
            }
        }, this);

        app.setIAppStatusListener(new IAppStatusListener() {
            @Override
            public boolean onStop() {
                rootView.removeView(app.obtainWebAppRootView().obtainMainView());
                return false;
            }

            @Override
            public void onStart() {
                // ToDo
            }

            @Override
            public void onPause(IApp arg0, IApp arg1) {
                // ToDo
            }

            @Override
            public String onStoped(boolean arg0, String arg1) {
                return null;
            }
        });
    }

    @Override
    public void onCoreReady(ICore coreHandler) {
        SDK.initSDK(coreHandler);
        SDK.requestAllFeature();
        ImageLoaderUtil.initImageLoaderL(activity);
    }

    @Override
    public boolean onCoreStop() {
        return false;
    }

    @Override
    public Object onCreateSplash(Context pContextWrapper) {
        splashView = new FrameLayout(activity);
        splashView.setBackgroundResource(RInformation.DRAWABLE_SPLASH);
        rootView.addView(splashView);
        return null;
    }

    @Override
    public void onCloseSplash() {
        rootView.removeView(splashView);
    }
}
