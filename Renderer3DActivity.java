package com.leophysics.dhim3drenderer;

import android.app.*;
import android.os.*;
import android.webkit.*;

public class Renderer3DActivity extends Activity
 {
	 WebView webview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.plot3d);
		webview=findViewById(R.id.plot3dWebView);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setAllowFileAccess(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setSupportZoom(true);
		webview.getSettings().setAllowFileAccessFromFileURLs(true);
		String html=getIntent().getStringExtra("htmlData");
		webview.loadDataWithBaseURL(null,html,"text/html","UTF-8",null);
		
    }
}


